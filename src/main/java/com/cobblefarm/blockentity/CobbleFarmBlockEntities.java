package com.cobblefarm.blockentity;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.block.CobbleFarmBlocks;
import com.cobblefarm.block.FarmTier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CobbleFarmBlockEntities {

    public static BlockEntityType<CobbleFarmBlockEntity> COBBLEFARM;

    public static void register() {
        COBBLEFARM = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                CobbleFarm.id("cobblefarm"),
                BlockEntityType.Builder.create(
                        (pos, state) -> {
                            FarmTier tier = FarmTier.IRON;
                            if (state.isOf(CobbleFarmBlocks.COBBLEFARM_GOLD)) tier = FarmTier.GOLD;
                            else if (state.isOf(CobbleFarmBlocks.COBBLEFARM_DIAMOND)) tier = FarmTier.DIAMOND;
                            else if (state.isOf(CobbleFarmBlocks.COBBLEFARM_EMERALD)) tier = FarmTier.EMERALD;
                            else if (state.isOf(CobbleFarmBlocks.COBBLEFARM_NETHERITE)) tier = FarmTier.NETHERITE;
                            return new CobbleFarmBlockEntity(pos, state, tier);
                        },
                        CobbleFarmBlocks.COBBLEFARM_IRON,
                        CobbleFarmBlocks.COBBLEFARM_GOLD,
                        CobbleFarmBlocks.COBBLEFARM_DIAMOND,
                        CobbleFarmBlocks.COBBLEFARM_EMERALD,
                        CobbleFarmBlocks.COBBLEFARM_NETHERITE
                ).build()
        );

        CobbleFarm.LOGGER.info("Registered CobbleFarm block entities.");
    }
}
