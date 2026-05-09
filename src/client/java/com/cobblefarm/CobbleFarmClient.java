package com.cobblefarm;

import com.cobblefarm.screen.CobbleFarmScreenHandlers;
import com.cobblefarm.screen.PokemonFarmScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class CobbleFarmClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(CobbleFarmScreenHandlers.POKEMON_FARM_SCREEN_HANDLER,
                PokemonFarmScreen::new);

        CobbleFarm.LOGGER.info("CobbleFarm client initialized.");
    }
}
