package com.cobblefarm.screen;

import com.cobblefarm.CobbleFarm;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class CobbleFarmScreenHandlers {

    public static ScreenHandlerType<PokemonFarmScreenHandler> POKEMON_FARM_SCREEN_HANDLER;

    public static void register() {
        POKEMON_FARM_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                CobbleFarm.id("pokemon_farm"),
                new ScreenHandlerType<>(PokemonFarmScreenHandler::new)
        );
        CobbleFarm.LOGGER.info("Registered CobbleFarm screen handlers.");
    }
}
