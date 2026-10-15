package com.cobblefarm.client.render;

import com.cobblefarm.block.CobbleFarmBlock;
import com.cobblefarm.blockentity.CobbleFarmBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class CobbleFarmRenderer implements BlockEntityRenderer<CobbleFarmBlockEntity> {

    public CobbleFarmRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(CobbleFarmBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        LivingEntity livingEntity = entity.getLivingEntity(entity.getWorld());
        if (livingEntity == null) return;

        // Drive animations by updating age based on world time
        livingEntity.age = (int) (entity.getWorld().getTime());

        Direction facing = entity.getCachedState().get(CobbleFarmBlock.FACING);
        float baseAngle = facing.asRotation();
        float spin = (entity.getWorld().getTime() + tickDelta) * 1.5f;

        matrices.push();
        
        // Translate to the center bottom of the block
        matrices.translate(0.5, 0.125, 0.5);

        // Calculate scaling
        float scale = 0.53125f;
        float g = Math.max(livingEntity.getWidth(), livingEntity.getHeight());
        if (g > 1.0f) {
            scale /= g;
        }
        matrices.scale(scale, scale, scale);

        // Continuous smooth rotation
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(baseAngle + spin));

        // Render the entity
        MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                livingEntity,
                0.0, 0.0, 0.0,
                0.0f, tickDelta,
                matrices, vertexConsumers, light
        );

        matrices.pop();
    }
}

