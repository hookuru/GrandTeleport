package dev.codex.gtaliketeleport;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

final class GtaLikeTeleportClientNetworking {
    private GtaLikeTeleportClientNetworking() {
    }

    static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(
                GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload.TYPE,
                (payload, context) -> context.client().execute(() -> GtaLikeTeleportClient.handleServerTeleportRequest(payload))
        );
    }

    static boolean canSendServerTeleportAck() {
        try {
            return ClientPlayNetworking.canSend(GtaLikeTeleportNetworkPayloads.ServerTeleportAckPayload.TYPE);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isServerSideTeleportAvailable() {
        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) {
            return true;
        }
        if (client.hasSingleplayerServer()) {
            return true;
        }
        return canSendServerTeleportAck();
    }

    static void sendServerTeleportAck(long requestId) {
        if (!canSendServerTeleportAck()) {
            return;
        }
        ClientPlayNetworking.send(new GtaLikeTeleportNetworkPayloads.ServerTeleportAckPayload(requestId));
    }

    static void sendBypassNextServerTeleport() {
        try {
            if (ClientPlayNetworking.canSend(GtaLikeTeleportNetworkPayloads.BypassNextServerTeleportPayload.TYPE)) {
                ClientPlayNetworking.send(new GtaLikeTeleportNetworkPayloads.BypassNextServerTeleportPayload());
            }
        } catch (IllegalStateException | IllegalArgumentException ignored) {
        }
    }

    static Vec3 targetFeet(GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload payload) {
        return new Vec3(payload.x(), payload.y(), payload.z());
    }

    static String targetDimensionId(GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload payload) {
        String dimension = payload.dimension();
        return DimensionIds.normalize(dimension);
    }
}
