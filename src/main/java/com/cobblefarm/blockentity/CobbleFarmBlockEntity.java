package com.cobblefarm.blockentity;

import com.cobblefarm.block.FarmTier;
import com.cobblefarm.item.FarmBallItem;
import com.cobblefarm.loot.PokemonLootHelper;
import com.cobblefarm.screen.CobbleFarmScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CobbleFarmBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {

    private final FarmTier tier;
    private final SimpleInventory pokemonSlot = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            CobbleFarmBlockEntity.this.markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            }
        }
    };

    // Internal output buffer to prevent item loss when container below is full
    private final DefaultedList<ItemStack> outputBuffer = DefaultedList.ofSize(9, ItemStack.EMPTY);

    @Environment(EnvType.CLIENT)
    private LivingEntity cachedEntity;

    @Environment(EnvType.CLIENT)
    private String cachedPokemonKey = "";

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

    // -------------------------------------------------------------------------
    // Server tick
    // -------------------------------------------------------------------------

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (isPaused) return;

        ItemStack pokemon = pokemonSlot.getStack(0);
        if (pokemon.isEmpty() || !FarmBallItem.hasPokemon(pokemon)) return;

        String speciesId = FarmBallItem.getLootTable(pokemon);
        if (speciesId.isEmpty()) return;

        // Check if the Pokemon actually has drops in Cobblemon
        if (!PokemonLootHelper.hasDrops(speciesId)) {
            return; // Pause without taking durability
        }

        // Try to flush internal buffer to container below first
        flushBuffer((ServerWorld) world, pos);

        // If buffer is completely full and cannot be flushed, pause to prevent item loss
        if (isBufferFull()) {
            return;
        }

        currentTick++;

        if (currentTick >= tier.ticksPerCycle) {
            currentTick = 0;
            produceDrop((ServerWorld) world, pos, pokemon);

            // Apply damage to Farm Ball
            int damage = tier.getRandomDamage(world.random);
            if (damage > 0) {
                pokemon.damage(damage, (ServerWorld) world, null, item -> {
                    pokemonSlot.setStack(0, ItemStack.EMPTY);
                });

                if (pokemon.isEmpty()) {
                    pokemonSlot.setStack(0, ItemStack.EMPTY);
                }
            }
        }

        markDirty();
    }

    private void flushBuffer(ServerWorld world, BlockPos pos) {
        BlockPos below = pos.down();
        for (int i = 0; i < outputBuffer.size(); i++) {
            ItemStack stack = outputBuffer.get(i);
            if (stack.isEmpty()) continue;

            ItemStack remainder = insertToTarget(world, below, stack);
            outputBuffer.set(i, remainder);
        }
    }

    private boolean isBufferFull() {
        for (ItemStack stack : outputBuffer) {
            if (stack.isEmpty()) return false;
            if (stack.getCount() < stack.getMaxCount()) return false;
        }
        return true;
    }

    private void produceDrop(ServerWorld world, BlockPos pos, ItemStack pokemon) {
        String speciesId = FarmBallItem.getLootTable(pokemon);
        if (speciesId.isEmpty()) return;

        List<ItemStack> drops = PokemonLootHelper.generateDrops(world, speciesId, tier.speedMultiplier);
        BlockPos below = pos.down();

        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            ItemStack remainder = insertToTarget(world, below, drop);

            // If not completely inserted into target, store remainder into buffer
            if (!remainder.isEmpty()) {
                insertIntoBuffer(remainder);
            }
        }
    }

    /**
     * Inserts an item into the container below using Fabric Transfer API (primary)
     * with fallback to vanilla Inventory / SidedInventory / Double Chest.
     */
    private ItemStack insertToTarget(ServerWorld world, BlockPos targetPos, ItemStack incoming) {
        if (incoming.isEmpty()) return ItemStack.EMPTY;

        // 1. Try Fabric Transfer API (supports Sophisticated Storage, AE2, Drawers, etc.)
        var storage = ItemStorage.SIDED.find(world, targetPos, Direction.UP);
        if (storage != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = storage.insert(ItemVariant.of(incoming), incoming.getCount(), transaction);
                if (inserted > 0) {
                    transaction.commit();
                    int rem = incoming.getCount() - (int) inserted;
                    if (rem <= 0) return ItemStack.EMPTY;
                    ItemStack copy = incoming.copy();
                    copy.setCount(rem);
                    return copy;
                }
            }
        }

        // 2. Fallback to vanilla Inventory / ChestBlock handling
        Inventory inv = getTargetInventory(world, targetPos);
        if (inv != null) {
            return insertIntoVanillaInventory(inv, incoming);
        }

        return incoming;
    }

    @Nullable
    private Inventory getTargetInventory(World world, BlockPos targetPos) {
        BlockState state = world.getBlockState(targetPos);
        Block block = state.getBlock();

        // Handle double chest correctly
        if (block instanceof ChestBlock chestBlock) {
            return ChestBlock.getInventory(chestBlock, state, world, targetPos, true);
        }

        BlockEntity be = world.getBlockEntity(targetPos);
        if (be instanceof Inventory inventory) {
            return inventory;
        }
        return null;
    }

    private ItemStack insertIntoVanillaInventory(Inventory inv, ItemStack incoming) {
        ItemStack stack = incoming.copy();
        int size = inv.size();

        // Pass 1: Merge into existing stacks
        for (int slot = 0; slot < size && !stack.isEmpty(); slot++) {
            if (!canInsertSlot(inv, slot, stack)) continue;
            ItemStack existing = inv.getStack(slot);
            if (!existing.isEmpty() && ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                int space = Math.min(inv.getMaxCountPerStack(), existing.getMaxCount()) - existing.getCount();
                int transfer = Math.min(space, stack.getCount());
                if (transfer > 0) {
                    existing.increment(transfer);
                    stack.decrement(transfer);
                    inv.markDirty();
                }
            }
        }

        // Pass 2: Insert into empty slots
        for (int slot = 0; slot < size && !stack.isEmpty(); slot++) {
            if (!canInsertSlot(inv, slot, stack)) continue;
            if (inv.getStack(slot).isEmpty()) {
                int count = Math.min(stack.getCount(), Math.min(inv.getMaxCountPerStack(), stack.getMaxCount()));
                ItemStack insert = stack.copy();
                insert.setCount(count);
                inv.setStack(slot, insert);
                stack.decrement(count);
                inv.markDirty();
            }
        }

        return stack;
    }

    private boolean canInsertSlot(Inventory inv, int slot, ItemStack stack) {
        if (inv instanceof SidedInventory sided) {
            return sided.canInsert(slot, stack, Direction.UP) && inv.isValid(slot, stack);
        }
        return inv.isValid(slot, stack);
    }

    private void insertIntoBuffer(ItemStack incoming) {
        ItemStack stack = incoming.copy();
        for (int i = 0; i < outputBuffer.size() && !stack.isEmpty(); i++) {
            ItemStack existing = outputBuffer.get(i);
            if (existing.isEmpty()) {
                outputBuffer.set(i, stack.copy());
                stack.setCount(0);
                markDirty();
                return;
            } else if (ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                int space = existing.getMaxCount() - existing.getCount();
                int transfer = Math.min(space, stack.getCount());
                if (transfer > 0) {
                    existing.increment(transfer);
                    stack.decrement(transfer);
                    markDirty();
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Drop on block break
    // -------------------------------------------------------------------------

    public void dropContents(World world, BlockPos pos) {
        ItemStack pokemon = pokemonSlot.getStack(0);
        if (!pokemon.isEmpty()) {
            ItemScatterer.spawn(world, pos, pokemonSlot);
        }
        for (ItemStack buf : outputBuffer) {
            if (!buf.isEmpty()) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), buf);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Pause toggle
    // -------------------------------------------------------------------------

    public void togglePause() {
        isPaused = !isPaused;
        markDirty();
    }

    public boolean isPaused() { return isPaused; }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public SimpleInventory getPokemonSlot() { return pokemonSlot; }

    public FarmTier getTier() { return tier; }

    public PropertyDelegate getPropertyDelegate() { return propertyDelegate; }

    // -------------------------------------------------------------------------
    // Client-only: cached PokemonEntity for rendering
    // -------------------------------------------------------------------------

    @Environment(EnvType.CLIENT)
    @Nullable
    public LivingEntity getLivingEntity(World renderWorld) {
        ItemStack currentStack = pokemonSlot.getStack(0);

        if (currentStack.isEmpty() || !FarmBallItem.hasPokemon(currentStack)) {
            cachedEntity = null;
            cachedPokemonKey = "";
            return null;
        }

        String species = FarmBallItem.getSpecies(currentStack);
        boolean shiny  = FarmBallItem.isShiny(currentStack);
        int level      = FarmBallItem.getLevel(currentStack);
        String form    = FarmBallItem.getForm(currentStack);
        String key = species + ":" + form + ":" + shiny + ":" + level;

        if (key.equals(cachedPokemonKey) && cachedEntity != null) {
            return cachedEntity;
        }
        cachedPokemonKey = key;

        try {
            StringBuilder props = new StringBuilder(species);
            props.append(" level=").append(level);
            if (shiny) props.append(" shiny=yes");
            if (form != null && !form.isEmpty() && !form.equals("normal")) {
                props.append(" form=").append(form);
            }

            com.cobblemon.mod.common.pokemon.Pokemon pokemon =
                    com.cobblemon.mod.common.api.pokemon.PokemonProperties.Companion
                            .parse(props.toString()).create();

            com.cobblemon.mod.common.entity.pokemon.PokemonEntity entity =
                    com.cobblemon.mod.common.CobblemonEntities.POKEMON.create(renderWorld);

            if (entity != null) {
                entity.setPokemon(pokemon);
            }
            cachedEntity = entity;
        } catch (Exception e) {
            com.cobblefarm.CobbleFarm.LOGGER.error("Failed to create PokemonEntity for rendering", e);
            cachedEntity = null;
        }
        return cachedEntity;
    }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        ItemStack stack = pokemonSlot.getStack(0);
        if (!stack.isEmpty()) {
            nbt.put("pokemon_slot", stack.encode(registryLookup));
        }
        nbt.putBoolean("is_paused", isPaused);
        nbt.putInt("current_tick", currentTick);
        Inventories.writeNbt(nbt, outputBuffer, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("pokemon_slot")) {
            pokemonSlot.setStack(0,
                    ItemStack.fromNbt(registryLookup, nbt.get("pokemon_slot"))
                             .orElse(ItemStack.EMPTY));
        } else {
            pokemonSlot.setStack(0, ItemStack.EMPTY);
        }
        isPaused   = nbt.getBoolean("is_paused");
        currentTick = nbt.getInt("current_tick");
        outputBuffer.clear();
        Inventories.readNbt(nbt, outputBuffer, registryLookup);
    }

    // -------------------------------------------------------------------------
    // Sync packets
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // -------------------------------------------------------------------------
    // Extended Screen factory
    // -------------------------------------------------------------------------

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.cobblefarm.farm.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CobbleFarmScreenHandler(syncId, playerInventory, this.pos, this, propertyDelegate);
    }
}