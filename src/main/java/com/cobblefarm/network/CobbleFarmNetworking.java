package com.cobblefarm.network;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.blockentity.PokemonFarmBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public class CobbleFarmNetworking {

    public record PauseTogglePayload(BlockPos pos) implements CustomPayload {
        public static final Id<PauseTogglePayload> ID = new Id<>(CobbleFarm.id("pause_toggle"));
        public static final PacketCodec<RegistryByteBuf, PauseTogglePayload> CODEC = PacketCodec.tuple(
                BlockPos.PACKET_CODEC, PauseTogglePayload::pos,
                PauseTogglePayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(PauseTogglePayload.ID, PauseTogglePayload.CODEC);
    }

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PauseTogglePayload.ID, (payload, context) -> {
            BlockPos pos = payload.pos();
            context.server().execute(() -> {
                if (context.player().getWorld().getBlockEntity(pos) instanceof PokemonFarmBlockEntity farm) {
                    if (context.player().squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64) {
                        farm.togglePause();
                    }
                }
            });
        });
    }
}
