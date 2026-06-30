package dev.codex.gtaliketeleport;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

final class GtaLikeTeleportServerNetworking {
    private GtaLikeTeleportServerNetworking() {
    }

    static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(
                GtaLikeTeleportNetworkPayloads.ServerTeleportAckPayload.TYPE,
                (payload, player, responseSender) -> player.server.execute(() -> GtaLikeTeleportServer.handleTeleportAck(player, payload.requestId()))
        );
        ServerPlayNetworking.registerGlobalReceiver(
                GtaLikeTeleportNetworkPayloads.BypassNextServerTeleportPayload.TYPE,
                (payload, player, responseSender) -> player.server.execute(() -> GtaLikeTeleportServer.markNextServerTeleportBypassed(player))
        );
    }

    static boolean canSendStart(ServerPlayer player) {
        try {
            return ServerPlayNetworking.canSend(player, GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload.TYPE);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static void sendStart(ServerPlayer player, long requestId, int source, Vec3 targetFeet, ResourceKey<Level> targetDimension) {
        ServerPlayNetworking.send(player, new GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload(
                requestId,
                source,
                targetFeet.x(),
                targetFeet.y(),
                targetFeet.z(),
                dimensionId(targetDimension)
        ));
    }

    private static String dimensionId(ResourceKey<Level> targetDimension) {
        String dimension = DimensionIds.fromResourceKey(targetDimension);
        return dimension == null ? "" : dimension;
    }
}
