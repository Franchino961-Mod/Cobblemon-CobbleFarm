package com.cobblefarm.blockentity;

import com.cobblefarm.block.FarmTier;
import com.cobblefarm.item.CapturedPokemonItem;
import com.cobblefarm.loot.PokemonLootHelper;
import com.cobblefarm.network.CobbleFarmNetworking;
import com.cobblefarm.screen.PokemonFarmScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PokemonFarmBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final FarmTier tier;

    /** Slot 0: the inserted CapturedPokemonItem. */
    private final SimpleInventory pokemonSlot = new SimpleInventory(1);

    /** Internal item buffer. */
    private final DefaultedList<ItemStack> buffer;

    private boolean isPaused = false;
    private int currentTick = 0;

    /** Shared with the ScreenHandler for progress bar sync. */
    private final PropertyDelegate propertyDelegate;
    // Index 0 = currentTick, Index 1 = ticksPerCycle, Index 2 = isPaused (0/1), Index 3 = bufferCount
    private int bufferItemCount = 0;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Tick (server-side only — registered via BlockEntityTicker in the block)
    // -------------------------------------------------------------------------

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

        // Try push to container below every 20 ticks to avoid hopper-lag
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

    // -------------------------------------------------------------------------
    // Buffer management
    // -------------------------------------------------------------------------

    private void addToBuffer(ItemStack incoming) {
        int remaining = incoming.getCount();

        // Try to merge into existing stacks first
        for (int i = 0; i < buffer.size(); i++) {
            if (remaining <= 0) break;
            ItemStack existing = buffer.get(i);
            if (existing.isEmpty()) continue;
            if (ItemStack.canCombine(existing, incoming)) {
                int space = existing.getMaxCount() - existing.getCount();
                int transfer = Math.min(space, remaining);
                existing.increment(transfer);
                remaining -= transfer;
            }
        }

        // Fill empty slots
        for (int i = 0; i < buffer.size(); i++) {
            if (remaining <= 0) break;
            if (buffer.get(i).isEmpty()) {
                ItemStack copy = incoming.copy();
                copy.setCount(Math.min(remaining, incoming.getMaxCount()));
                buffer.set(i, copy);
                remaining -= copy.getCount();
            }
        }
        // If remaining > 0, buffer is full — items are lost (production pauses next cycle)
    }

    private void updateBufferCount() {
        bufferItemCount = 0;
        for (ItemStack s : buffer) {
            bufferItemCount += s.getCount();
        }
    }

    // -------------------------------------------------------------------------
    // Output to container below
    // -------------------------------------------------------------------------

    private void pushToContainerBelow(ServerWorld world, BlockPos pos) {
        BlockPos below = pos.down();
        var belowBe = world.getBlockEntity(below);
        if (!(belowBe instanceof net.minecraft.inventory.Inventory targetInv)) return;

        for (int i = 0; i < buffer.size(); i++) {
            ItemStack stack = buffer.get(i);
            if (stack.isEmpty()) continue;

            // Try to insert into target inventory
            for (int slot = 0; slot < targetInv.size(); slot++) {
                if (!targetInv.isValid(slot, stack)) continue;

                ItemStack targetStack = targetInv.getStack(slot);
                if (targetStack.isEmpty()) {
                    targetInv.setStack(slot, stack.copy());
                    buffer.set(i, ItemStack.EMPTY);
                    targetInv.markDirty();
                    break;
                } else if (ItemStack.canCombine(targetStack, stack)) {
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

    // -------------------------------------------------------------------------
    // Drop contents on block break
    // -------------------------------------------------------------------------

    public void dropContents(World world, BlockPos pos) {
        // Drop the Pokémon item
        ItemStack pokemon = pokemonSlot.getStack(0);
        if (!pokemon.isEmpty()) {
            net.minecraft.item.ItemScatterer.spawn(world, pos, pokemonSlot);
        }
        // Drop buffer
        SimpleInventory bufferInv = new SimpleInventory(buffer.toArray(new ItemStack[0]));
        net.minecraft.item.ItemScatterer.spawn(world, pos, bufferInv);
    }

    // -------------------------------------------------------------------------
    // Pause toggle (called from networking)
    // -------------------------------------------------------------------------

    public void togglePause() {
        isPaused = !isPaused;
        markDirty();
    }

    public boolean isPaused() { return isPaused; }

    // -------------------------------------------------------------------------
    // Pokémon slot access
    // -------------------------------------------------------------------------

    public SimpleInventory getPokemonSlot() { return pokemonSlot; }

    public DefaultedList<ItemStack> getBuffer() { return buffer; }

    public FarmTier getTier() { return tier; }

    public PropertyDelegate getPropertyDelegate() { return propertyDelegate; }

    // -------------------------------------------------------------------------
    // NBT serialisation
    // -------------------------------------------------------------------------

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("pokemon_slot", pokemonSlot.getStack(0).writeNbt(new NbtCompound()));
        nbt.putBoolean("is_paused", isPaused);
        nbt.putInt("current_tick", currentTick);

        // Serialise buffer
        NbtCompound bufferNbt = new NbtCompound();
        Inventories.writeNbt(bufferNbt, buffer);
        nbt.put("buffer", bufferNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("pokemon_slot")) {
            pokemonSlot.setStack(0, ItemStack.fromNbt(nbt.getCompound("pokemon_slot")));
        }
        isPaused = nbt.getBoolean("is_paused");
        currentTick = nbt.getInt("current_tick");

        if (nbt.contains("buffer")) {
            Inventories.readNbt(nbt.getCompound("buffer"), buffer);
        }

        updateBufferCount();
    }

    // -------------------------------------------------------------------------
    // Screen factory
    // -------------------------------------------------------------------------

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
