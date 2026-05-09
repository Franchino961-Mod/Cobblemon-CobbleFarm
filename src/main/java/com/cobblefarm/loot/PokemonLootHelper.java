package com.cobblefarm.loot;

import com.cobblefarm.CobbleFarm;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating Pokémon drops from Cobblemon loot tables.
 */
public class PokemonLootHelper {

    /**
     * Generates drops for a given loot table ID, applying the farm's speed multiplier.
     *
     * @param world           The server world (required for LootContext).
     * @param lootTableId     The resource location string of the Cobblemon loot table,
     *                        e.g. "cobblemon:entities/bulbasaur".
     * @param speedMultiplier The tier multiplier to apply to drop quantities.
     * @return A list of ItemStacks representing the generated drops.
     */
    public static List<ItemStack> generateDrops(ServerWorld world, String lootTableId, float speedMultiplier) {
        List<ItemStack> result = new ArrayList<>();

        try {
            Identifier id = new Identifier(lootTableId);
            LootTable lootTable = world.getServer().getLootManager().getTable(id);

            if (lootTable == LootTable.EMPTY) {
                CobbleFarm.LOGGER.warn("CobbleFarm: Loot table not found: {}", lootTableId);
                return result;
            }

            // Build a minimal LootContext — no entity needed for passive generation
            LootContext context = new LootContext.Builder(world)
                    .random(world.random)
                    .build(LootContextTypes.EMPTY);

            List<ItemStack> rawDrops = lootTable.generateLoot(context);

            // Apply speed multiplier to each drop quantity
            for (ItemStack drop : rawDrops) {
                if (drop.isEmpty()) continue;

                ItemStack copy = drop.copy();
                int newCount = Math.round(copy.getCount() * speedMultiplier);
                newCount = Math.max(1, Math.min(newCount, copy.getMaxCount() * 4)); // cap at 4× max stack
                copy.setCount(newCount);
                result.add(copy);
            }

        } catch (Exception e) {
            CobbleFarm.LOGGER.error("CobbleFarm: Error generating drops for loot table '{}': {}",
                    lootTableId, e.getMessage());
        }

        return result;
    }
}
