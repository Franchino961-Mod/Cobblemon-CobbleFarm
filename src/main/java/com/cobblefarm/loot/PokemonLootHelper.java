package com.cobblefarm.loot;

import com.cobblefarm.CobbleFarm;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PokemonLootHelper {

    public static List<ItemStack> generateDrops(ServerWorld world, String lootTableId, float speedMultiplier) {
        List<ItemStack> result = new ArrayList<>();

        try {
            Identifier id = Identifier.of(lootTableId);
            RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(key);

            if (lootTable == LootTable.EMPTY) {
                CobbleFarm.LOGGER.warn("CobbleFarm: Loot table not found: {}", lootTableId);
                return result;
            }

            LootContextParameterSet parameterSet = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, world.getSpawnPos().toCenterPos())
                    .build(LootContextTypes.EMPTY);

            List<ItemStack> rawDrops = lootTable.generateLoot(parameterSet);

            for (ItemStack drop : rawDrops) {
                if (drop.isEmpty()) continue;

                ItemStack copy = drop.copy();
                int newCount = Math.round(copy.getCount() * speedMultiplier);
                newCount = Math.max(1, Math.min(newCount, copy.getMaxCount() * 4));
                copy.setCount(newCount);
                result.add(copy);
            }

        } catch (Exception e) {
            CobbleFarm.LOGGER.error("CobbleFarm: Error generating drops for loot table '{}': {}",
                    lootTableId, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
