package com.cobblefarm.screen;

import com.cobblefarm.CobbleFarm;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public class CobbleFarmScreenHandlers {

    public static ScreenHandlerType<CobbleFarmScreenHandler> COBBLEFARM_SCREEN_HANDLER;

    public static void register() {
        COBBLEFARM_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                CobbleFarm.id("cobblefarm"),
                new ExtendedScreenHandlerType<>(CobbleFarmScreenHandler::new, BlockPos.PACKET_CODEC)
        );
        CobbleFarm.LOGGER.info("Registered CobbleFarm screen handlers.");
    }
}

