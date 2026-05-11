package com.cobblefarm.blockentity;

import com.cobblefarm.block.FarmTier;
import com.cobblefarm.item.FarmBallItem;
import com.cobblefarm.loot.PokemonLootHelper;
import com.cobblefarm.screen.CobbleFarmScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CobbleFarmBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private final FarmTier tier;
    private final SimpleInventory pokemonSlot = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            CobbleFarmBlockEntity.this.markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
            }
        }
    };

    private LivingEntity cachedEntity;
    private ItemStack lastStack = ItemStack.EMPTY;

    private boolean isPaused = false;
    private int currentTick = 0;
    private final PropertyDelegate propertyDelegate;

    public CobbleFarmBlockEntity(BlockPos pos, BlockState state, FarmTier tier) {
        super(CobbleFarmBlockEntities.COBBLEFARM, pos, state);
        this.tier = tier;

        this.propertyDelegate = new PropertyDelegate() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> currentTick;
                    case 1 -> tier.ticksPerCycle;
                    case 2 -> isPaused ? 1 : 0;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {
                switch (index) {
                    case 0 -> currentTick = value;
                    case 2 -> isPaused = (value == 1);
                }
            }
            @Override public int size() { return 3; }
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (isPaused) return;

        ItemStack pokemon = pokemonSlot.getStack(0);
        if (pokemon.isEmpty() || !FarmBallItem.hasPokemon(pokemon)) return;

        // Check if container below exists — if not, do not tick
        BlockPos below = pos.down();
        var belowBe = world.getBlockEntity(below);
        if (!(belowBe instanceof Inventory)) return;

        currentTick++;

        if (currentTick >= tier.ticksPerCycle) {
            currentTick = 0;
            produceDrop((ServerWorld) world, pos, pokemon);
        }

        markDirty();
    }

    private void produceDrop(ServerWorld world, BlockPos pos, ItemStack pokemon) {
        String lootTableId = FarmBallItem.getLootTable(pokemon);
        if (lootTableId.isEmpty()) return;

        List<ItemStack> drops = PokemonLootHelper.generateDrops(world, lootTableId, tier.speedMultiplier);

        BlockPos below = pos.down();
        var belowBe = world.getBlockEntity(below);
        if (!(belowBe instanceof Inventory targetInv)) return;

        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            insertIntoInventory(targetInv, drop);
        }

        targetInv.markDirty();
    }

    private void insertIntoInventory(Inventory inv, ItemStack incoming) {
        int remaining = incoming.getCount();

        // Try to merge with existing stacks first
        for (int slot = 0; slot < inv.size() && remaining > 0; slot++) {
            if (!inv.isValid(slot, incoming)) continue;
            ItemStack existing = inv.getStack(slot);
            if (!existing.isEmpty() && ItemStack.areItemsAndComponentsEqual(existing, incoming)) {
                int space = existing.getMaxCount() - existing.getCount();
                int transfer = Math.min(space, remaining);
                existing.increment(transfer);
                remaining -= transfer;
            }
        }

        // Then fill empty slots
        for (int slot = 0; slot < inv.size() && remaining > 0; slot++) {
            if (!inv.isValid(slot, incoming)) continue;
            if (inv.getStack(slot).isEmpty()) {
                ItemStack copy = incoming.copy();
                copy.setCount(Math.min(remaining, incoming.getMaxCount()));
                inv.setStack(slot, copy);
                remaining -= copy.getCount();
            }
        }
    }

    public void dropContents(World world, BlockPos pos) {
        ItemStack pokemon = pokemonSlot.getStack(0);
        if (!pokemon.isEmpty()) {
            ItemScatterer.spawn(world, pos, pokemonSlot);
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        markDirty();
    }

    public boolean isPaused() { return isPaused; }

    public SimpleInventory getPokemonSlot() { return pokemonSlot; }

    public FarmTier getTier() { return tier; }

    public PropertyDelegate getPropertyDelegate() { return propertyDelegate; }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        ItemStack stack = pokemonSlot.getStack(0);
        if (!stack.isEmpty()) {
            nbt.put("pokemon_slot", stack.encode(registryLookup));
        }
        nbt.putBoolean("is_paused", isPaused);
        nbt.putInt("current_tick", currentTick);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("pokemon_slot")) {
            pokemonSlot.setStack(0, ItemStack.fromNbt(registryLookup, nbt.get("pokemon_slot")).orElse(ItemStack.EMPTY));
        } else {
            pokemonSlot.setStack(0, ItemStack.EMPTY);
        }
        isPaused = nbt.getBoolean("is_paused");
        currentTick = nbt.getInt("current_tick");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public LivingEntity getLivingEntity(World renderWorld) {
        ItemStack currentStack = pokemonSlot.getStack(0);
        if (ItemStack.areEqual(currentStack, lastStack)) {
            return cachedEntity;
        }
        lastStack = currentStack.copy();

        if (currentStack.isEmpty() || !FarmBallItem.hasPokemon(currentStack)) {
            cachedEntity = null;
            return null;
        }

        try {
            String species = FarmBallItem.getSpecies(currentStack);
            boolean shiny = FarmBallItem.isShiny(currentStack);
            int level = FarmBallItem.getLevel(currentStack);
            String form = FarmBallItem.getForm(currentStack);
            
            StringBuilder props = new StringBuilder(species);
            props.append(" level=").append(level);
            if (shiny) props.append(" shiny=yes");
            if (form != null && !form.isEmpty() && !form.equals("normal")) {
                props.append(" form=").append(form);
            }

            com.cobblemon.mod.common.pokemon.Pokemon pokemon = com.cobblemon.mod.common.api.pokemon.PokemonProperties.Companion.parse(props.toString()).create();
            com.cobblemon.mod.common.entity.pokemon.PokemonEntity entity = com.cobblemon.mod.common.CobblemonEntities.POKEMON.create(renderWorld);
            if (entity != null) {
                entity.setPokemon(pokemon);
            }
            cachedEntity = entity;
        } catch (Exception e) {
            com.cobblefarm.CobbleFarm.LOGGER.error("Failed to parse Pokemon for block rendering", e);
            cachedEntity = null;
        }
        return cachedEntity;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.cobblefarm.farm.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CobbleFarmScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
}
