package com.cobblefarm.blockentity;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.block.CobbleFarmBlocks;
import com.cobblefarm.block.FarmTier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CobbleFarmBlockEntities {

    public static BlockEntityType<PokemonFarmBlockEntity> POKEMON_FARM;

    public static void register() {
        POKEMON_FARM = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                CobbleFarm.id("pokemon_farm"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> {
                            FarmTier tier = FarmTier.IRON;
                            if (state.isOf(CobbleFarmBlocks.POKEMON_FARM_GOLD)) tier = FarmTier.GOLD;
                            else if (state.isOf(CobbleFarmBlocks.POKEMON_FARM_DIAMOND)) tier = FarmTier.DIAMOND;
                            else if (state.isOf(CobbleFarmBlocks.POKEMON_FARM_EMERALD)) tier = FarmTier.EMERALD;
                            else if (state.isOf(CobbleFarmBlocks.POKEMON_FARM_NETHERITE)) tier = FarmTier.NETHERITE;
                            return new PokemonFarmBlockEntity(pos, state, tier);
                        },
                        CobbleFarmBlocks.POKEMON_FARM_IRON,
                        CobbleFarmBlocks.POKEMON_FARM_GOLD,
                        CobbleFarmBlocks.POKEMON_FARM_DIAMOND,
                        CobbleFarmBlocks.POKEMON_FARM_EMERALD,
                        CobbleFarmBlocks.POKEMON_FARM_NETHERITE
                ).build()
        );

        CobbleFarm.LOGGER.info("Registered CobbleFarm block entities.");
    }
}
