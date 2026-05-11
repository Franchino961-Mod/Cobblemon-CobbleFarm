package com.cobblefarm;

import com.cobblefarm.screen.CobbleFarmScreenHandlers;
import com.cobblefarm.screen.CobbleFarmScreen;
import com.cobblefarm.block.CobbleFarmBlocks;
import com.cobblefarm.blockentity.CobbleFarmBlockEntities;
import com.cobblefarm.client.render.CobbleFarmRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CobbleFarmClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(CobbleFarmScreenHandlers.COBBLEFARM_SCREEN_HANDLER,
                CobbleFarmScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(CobbleFarmBlocks.COBBLEFARM_IRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CobbleFarmBlocks.COBBLEFARM_GOLD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CobbleFarmBlocks.COBBLEFARM_DIAMOND, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CobbleFarmBlocks.COBBLEFARM_EMERALD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CobbleFarmBlocks.COBBLEFARM_NETHERITE, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(CobbleFarmBlockEntities.COBBLEFARM, CobbleFarmRenderer::new);

        CobbleFarm.LOGGER.info("CobbleFarm client initialized.");
    }
}
