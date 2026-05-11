package com.cobblefarm.loot;

import com.cobblefarm.CobbleFarm;
import com.cobblemon.mod.common.api.drop.DropEntry;
import com.cobblemon.mod.common.api.drop.ItemDropEntry;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokemonLootHelper {
    private static final Random RANDOM = new Random();

    public static List<ItemStack> generateDrops(ServerWorld world, String speciesId, float speedMultiplier) {
        List<ItemStack> result = new ArrayList<>();

        try {
            Identifier id = Identifier.of(speciesId);
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(id);

            if (species == null) {
                CobbleFarm.LOGGER.warn("CobbleFarm: Species not found for drops: {}", speciesId);
                return result;
            }

            // Get drops from the species' DropTable
            // getDrops(amount, pokemon)
            List<DropEntry> drops = species.getDrops().getDrops(species.getDrops().getAmount(), null);

            for (DropEntry entry : drops) {
                if (entry instanceof ItemDropEntry itemEntry) {
                    // Check probability (percentage is 0-100)
                    if (RANDOM.nextFloat() * 100 <= itemEntry.getPercentage()) {
                        Identifier itemIdent = Identifier.of(itemEntry.getItem().getNamespace(), itemEntry.getItem().getPath());
                        Item item = Registries.ITEM.get(itemIdent);
                        if (item == null) {
                            CobbleFarm.LOGGER.error("CobbleFarm: Could not find item for drop: {}", itemIdent);
                            continue;
                        }

                        int count;
                        if (itemEntry.getQuantityRange() != null) {
                            int min = itemEntry.getQuantityRange().getStart();
                            int max = itemEntry.getQuantityRange().getEndInclusive();
                            count = min + RANDOM.nextInt(max - min + 1);
                        } else {
                            count = itemEntry.getQuantity();
                        }

                        if (count <= 0) continue;

                        ItemStack stack = new ItemStack(item, count);
                        
                        // Apply speed multiplier
                        int newCount = (int) Math.floor(stack.getCount() * speedMultiplier);
                        if (newCount < 1 && stack.getCount() > 0 && RANDOM.nextFloat() < speedMultiplier) {
                            newCount = 1;
                        }
                        
                        if (newCount > 0) {
                            stack.setCount(Math.min(newCount, stack.getMaxCount() * 64));
                            result.add(stack);
                        }
                    }
                }
            }

        } catch (Exception e) {
            CobbleFarm.LOGGER.error("CobbleFarm: Error generating drops for species '{}': {}",
                    speciesId, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
