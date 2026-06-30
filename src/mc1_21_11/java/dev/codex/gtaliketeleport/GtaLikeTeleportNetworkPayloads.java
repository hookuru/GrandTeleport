package dev.codex.gtaliketeleport;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

final class GtaLikeTeleportNetworkPayloads {
    static final int SOURCE_EXTERNAL = 1;
    static final int SOURCE_WARP_PLATE = 2;

    private static boolean registered;

    private GtaLikeTeleportNetworkPayloads() {
    }

    static synchronized void register() {
        if (registered) {
            return;
        }

        PayloadTypeRegistry.playS2C().register(StartServerTeleportPayload.TYPE, StartServerTeleportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerTeleportAckPayload.TYPE, ServerTeleportAckPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BypassNextServerTeleportPayload.TYPE, BypassNextServerTeleportPayload.CODEC);
        registered = true;
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("gtalike_teleport", path);
    }

    record StartServerTeleportPayload(long requestId, int source, double x, double y, double z, String dimension) implements CustomPacketPayload {
        static final Type<StartServerTeleportPayload> TYPE = new Type<>(id("start_server_teleport"));
        static final StreamCodec<RegistryFriendlyByteBuf, StartServerTeleportPayload> CODEC = StreamCodec.ofMember(
                StartServerTeleportPayload::write,
                StartServerTeleportPayload::read
        );

        private void write(RegistryFriendlyByteBuf buffer) {
            buffer.writeLong(this.requestId);
            buffer.writeInt(this.source);
            buffer.writeDouble(this.x);
            buffer.writeDouble(this.y);
            buffer.writeDouble(this.z);
            buffer.writeUtf(this.dimension);
        }

        private static StartServerTeleportPayload read(RegistryFriendlyByteBuf buffer) {
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
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    record ServerTeleportAckPayload(long requestId) implements CustomPacketPayload {
        static final Type<ServerTeleportAckPayload> TYPE = new Type<>(id("server_teleport_ack"));
        static final StreamCodec<RegistryFriendlyByteBuf, ServerTeleportAckPayload> CODEC = StreamCodec.ofMember(
                ServerTeleportAckPayload::write,
                ServerTeleportAckPayload::read
        );

        private void write(RegistryFriendlyByteBuf buffer) {
            buffer.writeLong(this.requestId);
        }

        private static ServerTeleportAckPayload read(RegistryFriendlyByteBuf buffer) {
            return new ServerTeleportAckPayload(buffer.readLong());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    record BypassNextServerTeleportPayload() implements CustomPacketPayload {
        static final Type<BypassNextServerTeleportPayload> TYPE = new Type<>(id("bypass_next_server_teleport"));
        static final StreamCodec<RegistryFriendlyByteBuf, BypassNextServerTeleportPayload> CODEC = StreamCodec.unit(
                new BypassNextServerTeleportPayload()
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
