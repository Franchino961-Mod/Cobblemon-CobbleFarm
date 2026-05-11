package com.cobblefarm.block;

import com.cobblefarm.CobbleFarm;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class CobbleFarmBlocks {

    public static final CobbleFarmBlock COBBLEFARM_IRON = register("cobblefarm_iron",
            new CobbleFarmBlock(FarmTier.IRON, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                    .strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final CobbleFarmBlock COBBLEFARM_GOLD = register("cobblefarm_gold",
            new CobbleFarmBlock(FarmTier.GOLD, AbstractBlock.Settings.copy(Blocks.GOLD_BLOCK)
                    .strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final CobbleFarmBlock COBBLEFARM_DIAMOND = register("cobblefarm_diamond",
            new CobbleFarmBlock(FarmTier.DIAMOND, AbstractBlock.Settings.copy(Blocks.DIAMOND_BLOCK)
                    .strength(4.0f, 8.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final CobbleFarmBlock COBBLEFARM_EMERALD = register("cobblefarm_emerald",
            new CobbleFarmBlock(FarmTier.EMERALD, AbstractBlock.Settings.copy(Blocks.EMERALD_BLOCK)
                    .strength(4.0f, 8.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final CobbleFarmBlock COBBLEFARM_NETHERITE = register("cobblefarm_netherite",
            new CobbleFarmBlock(FarmTier.NETHERITE, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)
                    .strength(5.0f, 1200.0f).sounds(BlockSoundGroup.NETHERITE).requiresTool().nonOpaque()));

    private static <T extends CobbleFarmBlock> T register(String name, T block) {
        Registry.register(Registries.BLOCK, CobbleFarm.id(name), block);
        Registry.register(Registries.ITEM, CobbleFarm.id(name),
                new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void register() {
        CobbleFarm.LOGGER.info("Registering CobbleFarm blocks...");
    }
}
