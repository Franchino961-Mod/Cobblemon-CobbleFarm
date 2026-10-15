package com.cobblefarm.loot;

import com.cobblefarm.CobbleFarm;
import com.cobblemon.mod.common.api.drop.DropEntry;
import com.cobblemon.mod.common.api.drop.ItemDropEntry;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PokemonLootHelper {

    /**
     * Checks if the species has any configured drop entries.
     */
    public static boolean hasDrops(String speciesId) {
        try {
            Identifier id = Identifier.of(speciesId);
            Species species = PokemonSpecies.getByIdentifier(id);
            if (species == null) return false;
            return species.getDrops() != null && !species.getDrops().getEntries().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates drops for the given Cobblemon species, applying the farm tier's speed multiplier.
     *
     * @param world           Server world context.
     * @param speciesId       ResourceLocation string of the species, e.g. "cobblemon:bulbasaur".
     * @param speedMultiplier Tier multiplier applied to drop quantities.
     * @return List of ItemStacks to push into the container below the farm.
     */
    public static List<ItemStack> generateDrops(ServerWorld world, String speciesId, float speedMultiplier) {
        List<ItemStack> result = new ArrayList<>();

        try {
            Identifier id = Identifier.of(speciesId);
            Species species = PokemonSpecies.getByIdentifier(id);

            if (species == null) {
                CobbleFarm.LOGGER.warn("CobbleFarm: Species not found for drops: {}", speciesId);
                return result;
            }

            if (species.getDrops() == null || species.getDrops().getEntries().isEmpty()) {
                return result;
            }

            // Create a lightweight Pokemon instance to safely pass to getDrops so conditional checks won't throw NPE
            Pokemon dummyPokemon;
            try {
                dummyPokemon = PokemonProperties.Companion.parse(speciesId).create();
            } catch (Exception ignored) {
                dummyPokemon = null;
            }

            List<DropEntry> drops = species.getDrops().getDrops(species.getDrops().getAmount(), dummyPokemon);

            for (DropEntry entry : drops) {
                if (!(entry instanceof ItemDropEntry itemEntry)) continue;

                // Roll drop chance (percentage is 0–100)
                if (world.getRandom().nextFloat() * 100 > itemEntry.getPercentage()) continue;

                // Resolve item
                Identifier itemIdent = Identifier.of(
                        itemEntry.getItem().getNamespace(),
                        itemEntry.getItem().getPath()
                );
                Item item = Registries.ITEM.get(itemIdent);
                if (item == null) {
                    CobbleFarm.LOGGER.warn("CobbleFarm: Item not found for drop: {}", itemIdent);
                    continue;
                }

                // Resolve base count
                int count;
                if (itemEntry.getQuantityRange() != null) {
                    int min = itemEntry.getQuantityRange().getStart();
                    int max = itemEntry.getQuantityRange().getEndInclusive();
                    count = (min == max) ? min : min + world.getRandom().nextInt(max - min + 1);
                } else {
                    count = itemEntry.getQuantity();
                }
                if (count <= 0) continue;

                // Apply tier multiplier
                int newCount = (int) Math.floor(count * speedMultiplier);
                float remainder = (count * speedMultiplier) - newCount;
                if (remainder > 0 && world.getRandom().nextFloat() < remainder) {
                    newCount++;
                }
                if (newCount <= 0) continue;

                newCount = Math.min(newCount, item.getDefaultStack().getMaxCount());
                result.add(new ItemStack(item, newCount));
            }

        } catch (Exception e) {
            CobbleFarm.LOGGER.error("CobbleFarm: Error generating drops for species '{}': {}",
                    speciesId, e.getMessage());
        }

        return result;
    }
}