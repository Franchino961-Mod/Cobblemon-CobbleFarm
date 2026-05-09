package com.cobblefarm.item;

import com.cobblefarm.CobbleFarm;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

/**
 * The Farm Ball item.
 *
 * Capture mechanics are handled via Cobblemon's event API in CobbleFarmEvents.
 * This class registers the item; the actual post-capture redirect logic
 * listens to CobblemonEvents.POKEMON_CAPTURED (or via PokeBallMixin as fallback).
 */
public class FarmBallItem extends Item {

    public FarmBallItem() {
        super(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    }
}
