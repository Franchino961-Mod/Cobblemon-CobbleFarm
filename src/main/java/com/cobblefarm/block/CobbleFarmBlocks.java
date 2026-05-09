package com.cobblefarm.block;

import com.cobblefarm.CobbleFarm;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class CobbleFarmBlocks {

    public static final PokemonFarmBlock POKEMON_FARM_IRON = register("pokemon_farm_iron",
            new PokemonFarmBlock(FarmTier.IRON, AbstractBlock.Settings.of(Material.METAL)
                    .strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final PokemonFarmBlock POKEMON_FARM_GOLD = register("pokemon_farm_gold",
            new PokemonFarmBlock(FarmTier.GOLD, AbstractBlock.Settings.of(Material.METAL)
                    .strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final PokemonFarmBlock POKEMON_FARM_DIAMOND = register("pokemon_farm_diamond",
            new PokemonFarmBlock(FarmTier.DIAMOND, AbstractBlock.Settings.of(Material.METAL)
                    .strength(4.0f, 8.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final PokemonFarmBlock POKEMON_FARM_EMERALD = register("pokemon_farm_emerald",
            new PokemonFarmBlock(FarmTier.EMERALD, AbstractBlock.Settings.of(Material.METAL)
                    .strength(4.0f, 8.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final PokemonFarmBlock POKEMON_FARM_NETHERITE = register("pokemon_farm_netherite",
            new PokemonFarmBlock(FarmTier.NETHERITE, AbstractBlock.Settings.of(Material.METAL)
                    .strength(5.0f, 1200.0f).sounds(BlockSoundGroup.NETHERITE).requiresTool()));

    private static <T extends PokemonFarmBlock> T register(String name, T block) {
        Registry.register(Registries.BLOCK, CobbleFarm.id(name), block);
        Registry.register(Registries.ITEM, CobbleFarm.id(name),
                new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    public static void register() {
        CobbleFarm.LOGGER.info("Registering CobbleFarm blocks...");
    }
}
