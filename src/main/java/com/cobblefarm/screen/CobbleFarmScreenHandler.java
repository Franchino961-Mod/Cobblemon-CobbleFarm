package com.cobblefarm.screen;

import com.cobblefarm.blockentity.CobbleFarmBlockEntity;
import com.cobblefarm.item.FarmBallItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CobbleFarmScreenHandler extends ScreenHandler {

    private final Inventory pokemonSlot;
    private final PropertyDelegate propertyDelegate;
    private final CobbleFarmBlockEntity blockEntity;

    public CobbleFarmScreenHandler(int syncId, PlayerInventory playerInventory,
                                   CobbleFarmBlockEntity blockEntity,
                                   PropertyDelegate propertyDelegate) {
        super(CobbleFarmScreenHandlers.COBBLEFARM_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
        if (blockEntity != null) {
            this.pokemonSlot = blockEntity.getPokemonSlot();
        } else {
            this.pokemonSlot = new SimpleInventory(1);
        }
        this.propertyDelegate = propertyDelegate;

        addProperties(propertyDelegate);

        this.addSlot(new Slot(pokemonSlot, 0, 71, 33) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return FarmBallItem.hasPokemon(stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public CobbleFarmScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, new ArrayPropertyDelegate(3));
    }

    public int getCurrentTick() { return propertyDelegate.get(0); }
    public int getTicksPerCycle() { return propertyDelegate.get(1); }
    public boolean isPaused() { return propertyDelegate.get(2) == 1; }

    public float getProgress() {
        int cycle = getTicksPerCycle();
        if (cycle == 0) return 0f;
        return (float) getCurrentTick() / cycle;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index == 0) {
                if (!this.insertItem(stack, 1, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (FarmBallItem.hasPokemon(stack)) {
                    if (!this.insertItem(stack, 0, 1, false)) return ItemStack.EMPTY;
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

    public CobbleFarmBlockEntity getBlockEntity() { return blockEntity; }
}
