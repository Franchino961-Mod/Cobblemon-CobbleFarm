package com.cobblefarm.item;

import com.cobblefarm.CobbleFarm;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CobbleFarmItems {

    public static final FarmBallItem FARM_BALL = register("farm_ball", new FarmBallItem());
    public static final CapturedPokemonItem CAPTURED_POKEMON_ITEM =
            register("captured_pokemon_item", new CapturedPokemonItem());

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, CobbleFarm.id(name), item);
    }

    public static void register() {
        CobbleFarm.LOGGER.info("Registering CobbleFarm items...");
        // Static initialisation handles registration above
    }
}
