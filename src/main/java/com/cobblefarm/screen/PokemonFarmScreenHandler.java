package com.cobblefarm.screen;

import com.cobblefarm.blockentity.PokemonFarmBlockEntity;
import com.cobblefarm.item.CapturedPokemonItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PokemonFarmScreenHandler extends ScreenHandler {

    private final SimpleInventory pokemonSlot;
    private final PropertyDelegate propertyDelegate;
    private final PokemonFarmBlockEntity blockEntity;

    // Server-side constructor (called from BlockEntity)
    public PokemonFarmScreenHandler(int syncId, PlayerInventory playerInventory,
                                    PokemonFarmBlockEntity blockEntity,
                                    PropertyDelegate propertyDelegate) {
        super(CobbleFarmScreenHandlers.POKEMON_FARM_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
        if (blockEntity != null) {
            this.pokemonSlot = blockEntity.getPokemonSlot();
        } else {
            this.pokemonSlot = new SimpleInventory(1);
        }
        this.propertyDelegate = propertyDelegate;

        addProperties(propertyDelegate);

        // Pokémon input slot (only accepts CapturedPokemonItem)
        addSlot(new Slot(pokemonSlot, 0, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof CapturedPokemonItem;
            }
        });

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Client-side constructor (called from client screen factory)
    public PokemonFarmScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, new PropertyDelegate() {
            private final int[] values = new int[5];
            @Override public int get(int index) { return values[index]; }
            @Override public void set(int index, int value) { values[index] = value; }
            @Override public int size() { return 5; }
        });
    }

    // -------------------------------------------------------------------------
    // Property accessors (used by the Screen to render progress/state)
    // -------------------------------------------------------------------------

    public int getCurrentTick() { return propertyDelegate.get(0); }
    public int getTicksPerCycle() { return propertyDelegate.get(1); }
    public boolean isPaused() { return propertyDelegate.get(2) == 1; }
    public int getBufferCount() { return propertyDelegate.get(3); }
    public int getBufferCapacity() { return propertyDelegate.get(4); }

    public float getProgress() {
        int cycle = getTicksPerCycle();
        if (cycle == 0) return 0f;
        return (float) getCurrentTick() / cycle;
    }

    // -------------------------------------------------------------------------
    // Shift-click support
    // -------------------------------------------------------------------------

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index == 0) {
                // From Pokémon slot → player inventory
                if (!insertItem(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
            } else {
                // From player inventory → Pokémon slot (only CapturedPokemonItem)
                if (stack.getItem() instanceof CapturedPokemonItem) {
                    if (!insertItem(stack, 0, 1, false)) return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }

        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public PokemonFarmBlockEntity getBlockEntity() { return blockEntity; }
}
