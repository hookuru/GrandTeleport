package dev.codex.gtaliketeleport;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

final class GtaLikeTeleportNetworkPayloads {
    static final int SOURCE_EXTERNAL = 1;
    static final int SOURCE_WARP_PLATE = 2;

    private GtaLikeTeleportNetworkPayloads() {
    }

    static void register() {
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation("gtalike_teleport", path);
    }

    record StartServerTeleportPayload(long requestId, int source, double x, double y, double z, String dimension) implements FabricPacket {
        static final PacketType<StartServerTeleportPayload> TYPE = PacketType.create(
                id("start_server_teleport"),
                StartServerTeleportPayload::read
        );

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeLong(this.requestId);
            buffer.writeInt(this.source);
            buffer.writeDouble(this.x);
            buffer.writeDouble(this.y);
            buffer.writeDouble(this.z);
            buffer.writeUtf(this.dimension);
        }

        private static StartServerTeleportPayload read(FriendlyByteBuf buffer) {
            return new StartServerTeleportPayload(
                    buffer.readLong(),
                    buffer.readInt(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readUtf()
            );
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

    record ServerTeleportAckPayload(long requestId) implements FabricPacket {
        static final PacketType<ServerTeleportAckPayload> TYPE = PacketType.create(
                id("server_teleport_ack"),
                ServerTeleportAckPayload::read
        );

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeLong(this.requestId);
        }

        private static ServerTeleportAckPayload read(FriendlyByteBuf buffer) {
            return new ServerTeleportAckPayload(buffer.readLong());
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

    record BypassNextServerTeleportPayload() implements FabricPacket {
        static final PacketType<BypassNextServerTeleportPayload> TYPE = PacketType.create(
                id("bypass_next_server_teleport"),
                BypassNextServerTeleportPayload::read
        );

        @Override
        public void write(FriendlyByteBuf buffer) {
        }

        private static BypassNextServerTeleportPayload read(FriendlyByteBuf buffer) {
            return new BypassNextServerTeleportPayload();
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
