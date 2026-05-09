package com.cobblefarm.blockentity;

import com.cobblefarm.block.FarmTier;
import com.cobblefarm.item.CapturedPokemonItem;
import com.cobblefarm.loot.PokemonLootHelper;
import com.cobblefarm.screen.PokemonFarmScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PokemonFarmBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private final FarmTier tier;
    private final SimpleInventory pokemonSlot = new SimpleInventory(1);
    private final DefaultedList<ItemStack> buffer;

    private boolean isPaused = false;
    private int currentTick = 0;
    private final PropertyDelegate propertyDelegate;
    private int bufferItemCount = 0;

    public PokemonFarmBlockEntity(BlockPos pos, BlockState state, FarmTier tier) {
        super(CobbleFarmBlockEntities.POKEMON_FARM, pos, state);
        this.tier = tier;
        this.buffer = DefaultedList.ofSize(tier.bufferCapacity, ItemStack.EMPTY);

        this.propertyDelegate = new PropertyDelegate() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> currentTick;
                    case 1 -> tier.ticksPerCycle;
                    case 2 -> isPaused ? 1 : 0;
                    case 3 -> bufferItemCount;
                    case 4 -> tier.bufferCapacity;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {
                switch (index) {
                    case 0 -> currentTick = value;
                    case 2 -> isPaused = (value == 1);
                    case 3 -> bufferItemCount = value;
                }
            }
            @Override public int size() { return 5; }
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (isPaused) return;

        ItemStack pokemon = pokemonSlot.getStack(0);
        if (pokemon.isEmpty() || !(pokemon.getItem() instanceof CapturedPokemonItem)) return;

        currentTick++;

        if (currentTick >= tier.ticksPerCycle) {
            currentTick = 0;
            produceDrop(world, pokemon);
        }

        if (world.getTime() % 20 == 0) {
            pushToContainerBelow((ServerWorld) world, pos);
        }

        markDirty();
    }

    private void produceDrop(World world, ItemStack pokemon) {
        String lootTableId = CapturedPokemonItem.getLootTable(pokemon);
        if (lootTableId.isEmpty()) return;

        List<ItemStack> drops = PokemonLootHelper.generateDrops((ServerWorld) world, lootTableId, tier.speedMultiplier);

        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            addToBuffer(drop);
        }

        updateBufferCount();
    }

    private void addToBuffer(ItemStack incoming) {
        int remaining = incoming.getCount();

        for (int i = 0; i < buffer.size(); i++) {
            if (remaining <= 0) break;
            ItemStack existing = buffer.get(i);
            if (existing.isEmpty()) continue;
            if (ItemStack.areItemsAndComponentsEqual(existing, incoming)) {
                int space = existing.getMaxCount() - existing.getCount();
                int transfer = Math.min(space, remaining);
                existing.increment(transfer);
                remaining -= transfer;
            }
        }

        for (int i = 0; i < buffer.size(); i++) {
            if (remaining <= 0) break;
            if (buffer.get(i).isEmpty()) {
                ItemStack copy = incoming.copy();
                copy.setCount(Math.min(remaining, incoming.getMaxCount()));
                buffer.set(i, copy);
                remaining -= copy.getCount();
            }
        }
    }

    private void updateBufferCount() {
        bufferItemCount = 0;
        for (ItemStack s : buffer) {
            bufferItemCount += s.getCount();
        }
    }

    private void pushToContainerBelow(ServerWorld world, BlockPos pos) {
        BlockPos below = pos.down();
        var belowBe = world.getBlockEntity(below);
        if (!(belowBe instanceof Inventory targetInv)) return;

        for (int i = 0; i < buffer.size(); i++) {
            ItemStack stack = buffer.get(i);
            if (stack.isEmpty()) continue;

            for (int slot = 0; slot < targetInv.size(); slot++) {
                if (!targetInv.isValid(slot, stack)) continue;

                ItemStack targetStack = targetInv.getStack(slot);
                if (targetStack.isEmpty()) {
                    targetInv.setStack(slot, stack.copy());
                    buffer.set(i, ItemStack.EMPTY);
                    targetInv.markDirty();
                    break;
                } else if (ItemStack.areItemsAndComponentsEqual(targetStack, stack)) {
                    int space = targetStack.getMaxCount() - targetStack.getCount();
                    if (space > 0) {
                        int transfer = Math.min(space, stack.getCount());
                        targetStack.increment(transfer);
                        stack.decrement(transfer);
                        if (stack.isEmpty()) buffer.set(i, ItemStack.EMPTY);
                        targetInv.markDirty();
                        break;
                    }
                }
            }
        }
        updateBufferCount();
    }

    public void dropContents(World world, BlockPos pos) {
        ItemStack pokemon = pokemonSlot.getStack(0);
        if (!pokemon.isEmpty()) {
            ItemScatterer.spawn(world, pos, pokemonSlot);
        }
        SimpleInventory bufferInv = new SimpleInventory(buffer.toArray(new ItemStack[0]));
        ItemScatterer.spawn(world, pos, bufferInv);
    }

    public void togglePause() {
        isPaused = !isPaused;
        markDirty();
    }

    public boolean isPaused() { return isPaused; }

    public SimpleInventory getPokemonSlot() { return pokemonSlot; }

    public DefaultedList<ItemStack> getBuffer() { return buffer; }

    public FarmTier getTier() { return tier; }

    public PropertyDelegate getPropertyDelegate() { return propertyDelegate; }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("pokemon_slot", pokemonSlot.getStack(0).encode(registryLookup));
        nbt.putBoolean("is_paused", isPaused);
        nbt.putInt("current_tick", currentTick);

        NbtCompound bufferNbt = new NbtCompound();
        Inventories.writeNbt(bufferNbt, buffer, registryLookup);
        nbt.put("buffer", bufferNbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("pokemon_slot")) {
            pokemonSlot.setStack(0, ItemStack.fromNbt(registryLookup, nbt.getCompound("pokemon_slot")).orElse(ItemStack.EMPTY));
        }
        isPaused = nbt.getBoolean("is_paused");
        currentTick = nbt.getInt("current_tick");

        if (nbt.contains("buffer")) {
            Inventories.readNbt(nbt.getCompound("buffer"), buffer, registryLookup);
        }

        updateBufferCount();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.cobblefarm.farm.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PokemonFarmScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
}
