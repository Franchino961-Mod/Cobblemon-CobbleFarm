package com.cobblefarm.item;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class FarmBallItem extends Item {

    public FarmBallItem() {
        super(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    }
}
