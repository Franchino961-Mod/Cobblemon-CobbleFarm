package com.cobblefarm.network;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.blockentity.PokemonFarmBlockEntity;
import com.cobblefarm.screen.PokemonFarmScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CobbleFarmNetworking {

    /** C2S: player toggles pause on the farm. Payload: BlockPos. */
    public static final Identifier PAUSE_TOGGLE_C2S = CobbleFarm.id("pause_toggle");

    public static void registerServerPackets() {
        // Handle pause toggle from client
        ServerPlayNetworking.registerGlobalReceiver(PAUSE_TOGGLE_C2S, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                if (player.getWorld().getBlockEntity(pos) instanceof PokemonFarmBlockEntity farm) {
                    // Security: check player is near enough
                    if (player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64) {
                        farm.togglePause();
                    }
                }
            });
        });

        CobbleFarm.LOGGER.info("Registered CobbleFarm server networking packets.");
    }

    /** Helper: create a pause toggle packet for the given block position. */
    public static PacketByteBuf createPauseTogglePacket(BlockPos pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        return buf;
    }
}
