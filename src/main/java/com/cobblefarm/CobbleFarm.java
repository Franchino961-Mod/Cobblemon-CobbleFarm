package com.cobblefarm;

import com.cobblefarm.block.CobbleFarmBlocks;
import com.cobblefarm.blockentity.CobbleFarmBlockEntities;
import com.cobblefarm.item.CobbleFarmItems;
import com.cobblefarm.network.CobbleFarmNetworking;
import com.cobblefarm.screen.CobbleFarmScreenHandlers;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobbleFarm implements ModInitializer {

    public static final String MOD_ID = "cobblefarm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("CobbleFarm initializing...");

        CobbleFarmItems.register();
        CobbleFarmBlocks.register();
        CobbleFarmBlockEntities.register();
        CobbleFarmScreenHandlers.register();
        CobbleFarmNetworking.registerPayloads();
        CobbleFarmNetworking.registerServerPackets();

        // Handle Pokémon capture with Farm Ball
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
            ServerPlayerEntity player = event.getPlayer();
            Pokemon pokemon = event.getPokemon();
            
            String species = pokemon.getSpecies().getResourceIdentifier().toString();
            String form = pokemon.getForm().getName();
            int level = pokemon.getLevel();
            boolean shiny = pokemon.getShiny();
            String displayName = pokemon.getDisplayName(true).getString();
            String lootTable = "cobblemon:entities/" + pokemon.getSpecies().getName().toLowerCase();

            ItemStack capturedItem = com.cobblefarm.item.CapturedPokemonItem.create(
                species, form, level, shiny, displayName, lootTable
            );

            if (!player.getInventory().insertStack(capturedItem)) {
                player.dropItem(capturedItem, false);
            }
            
            return kotlin.Unit.INSTANCE;
        });

        // Register creative tab
        Registry.register(Registries.ITEM_GROUP, id("cobblefarm"), FabricItemGroup.builder()
                .displayName(Text.translatable("itemgroup.cobblefarm"))
                .icon(() -> new ItemStack(CobbleFarmItems.FARM_BALL))
                .entries((context, entries) -> {
                    entries.add(CobbleFarmItems.FARM_BALL);
                    entries.add(CobbleFarmItems.CAPTURED_POKEMON_ITEM);
                    entries.add(CobbleFarmBlocks.POKEMON_FARM_IRON.asItem());
                    entries.add(CobbleFarmBlocks.POKEMON_FARM_GOLD.asItem());
                    entries.add(CobbleFarmBlocks.POKEMON_FARM_DIAMOND.asItem());
                    entries.add(CobbleFarmBlocks.POKEMON_FARM_EMERALD.asItem());
                    entries.add(CobbleFarmBlocks.POKEMON_FARM_NETHERITE.asItem());
                })
                .build());

        LOGGER.info("CobbleFarm initialized!");
    }
}
