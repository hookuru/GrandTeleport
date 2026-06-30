package dev.codex.gtaliketeleport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class GtaLikeTeleportServer implements ModInitializer {
    private static final int BYPASS_TICKS = 20;
    private static final int ACK_TIMEOUT_TICKS = 200;
    private static final AtomicLong NEXT_REQUEST_ID = new AtomicLong(1L);
    private static final Map<UUID, Integer> bypassNextTeleportTicks = new HashMap<>();
    private static final Map<Long, PendingTeleport> pendingTeleports = new HashMap<>();
    private static final Set<UUID> pendingPlayers = new HashSet<>();
    private static final ThreadLocal<Boolean> executingDelayedTeleport = ThreadLocal.withInitial(() -> false);

    @Override
    public void onInitialize() {
        GtaLikeTeleportConfig.load();
        GtaLikeTeleportNetworkPayloads.register();
        GtaLikeTeleportServerNetworking.registerReceivers();
        ServerTickEvents.END_SERVER_TICK.register(GtaLikeTeleportServer::tick);
    }

    public static boolean tryDelayExternalTeleport(
            ServerPlayer player,
            ServerLevel level,
            double x,
            double y,
            double z,
            Set<Relative> relatives,
            float yaw,
            float pitch,
            boolean resetCamera
    ) {
        if (consumeServerTeleportBypass(player)) {
            return false;
        }
        if (pendingPlayers.contains(player.getUUID())) {
            return true;
        }
        if (!shouldStartServerTransition(player, GtaLikeTeleportNetworkPayloads.SOURCE_EXTERNAL)) {
            return false;
        }

        Vec3 targetFeet = resolveTargetFeet(player, x, y, z, relatives);
        return scheduleServerTransition(
                player,
                GtaLikeTeleportNetworkPayloads.SOURCE_EXTERNAL,
                targetFeet,
                level.dimension(),
                () -> player.teleportTo(level, x, y, z, relatives, yaw, pitch, resetCamera)
        );
    }

    private static Vec3 resolveTargetFeet(ServerPlayer player, double x, double y, double z, Set<Relative> relatives) {
        return new Vec3(
                resolveCoordinate(player.getX(), x, relatives, Relative.X),
                resolveCoordinate(player.getY(), y, relatives, Relative.Y),
                resolveCoordinate(player.getZ(), z, relatives, Relative.Z)
        );
    }

    private static double resolveCoordinate(double current, double target, Set<Relative> relatives, Relative axis) {
        return relatives != null && relatives.contains(axis) ? current + target : target;
    }
    static boolean scheduleServerTransition(ServerPlayer player, int source, Vec3 targetFeet, Runnable action) {
        return scheduleServerTransition(player, source, targetFeet, player == null ? null : player.level().dimension(), action);
    }

    static boolean scheduleServerTransition(ServerPlayer player, int source, Vec3 targetFeet, ResourceKey<Level> targetDimension, Runnable action) {
        if (consumeServerTeleportBypass(player)) {
            return false;
        }
        if (pendingPlayers.contains(player.getUUID())) {
            return true;
        }
        if (!shouldStartServerTransition(player, source)) {
            return false;
        }

        long requestId = NEXT_REQUEST_ID.getAndIncrement();
        PendingTeleport pending = new PendingTeleport(requestId, player, source, action);
        pendingTeleports.put(requestId, pending);
        pendingPlayers.add(player.getUUID());
        GtaLikeTeleportServerNetworking.sendStart(player, requestId, source, targetFeet, targetDimension);
        return true;
    }

    static void handleTeleportAck(ServerPlayer player, long requestId) {
        PendingTeleport pending = pendingTeleports.get(requestId);
        if (pending == null || !pending.player.getUUID().equals(player.getUUID())) {
            return;
        }

        pendingTeleports.remove(requestId);
        pendingPlayers.remove(player.getUUID());
        runPendingTeleport(pending);
    }

    static void markNextServerTeleportBypassed(ServerPlayer player) {
        if (player != null) {
            bypassNextTeleportTicks.put(player.getUUID(), BYPASS_TICKS);
        }
    }

    static void runWithServerTeleportBypass(ServerPlayer player, Runnable action) {
        markNextServerTeleportBypassed(player);
        executingDelayedTeleport.set(true);
        try {
            action.run();
        } finally {
            executingDelayedTeleport.set(false);
        }
    }

    private static boolean shouldStartServerTransition(ServerPlayer player, int source) {
        if (player == null || player.hasDisconnected() || !player.isAlive()) {
            return false;
        }
        if (!GtaLikeTeleportConfig.isEffectEnabled()) {
            return false;
        }
        if (source == GtaLikeTeleportNetworkPayloads.SOURCE_EXTERNAL && !GtaLikeTeleportConfig.isExternalTeleportTransitionsEnabled()) {
            return false;
        }
        if (source == GtaLikeTeleportNetworkPayloads.SOURCE_WARP_PLATE && !GtaLikeTeleportConfig.isWarpPlateTransitionsEnabled()) {
            return false;
        }
        return GtaLikeTeleportServerNetworking.canSendStart(player);
    }

    private static boolean consumeServerTeleportBypass(ServerPlayer player) {
        if (Boolean.TRUE.equals(executingDelayedTeleport.get())) {
            return true;
        }
        if (player == null) {
            return false;
        }
        Integer ticks = bypassNextTeleportTicks.remove(player.getUUID());
        return ticks != null && ticks > 0;
    }

    private static void tick(MinecraftServer server) {
        tickBypassEntries();
        tickPendingTeleports();
    }

    private static void tickBypassEntries() {
        Iterator<Map.Entry<UUID, Integer>> iterator = bypassNextTeleportTicks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int ticks = entry.getValue() - 1;
            if (ticks <= 0) {
                iterator.remove();
            } else {
                entry.setValue(ticks);
            }
        }
    }

    private static void tickPendingTeleports() {
        Iterator<Map.Entry<Long, PendingTeleport>> iterator = pendingTeleports.entrySet().iterator();
        while (iterator.hasNext()) {
            PendingTeleport pending = iterator.next().getValue();
            pending.age++;
            if (pending.age > ACK_TIMEOUT_TICKS) {
                iterator.remove();
                pendingPlayers.remove(pending.player.getUUID());
                runPendingTeleport(pending);
            }
        }
    }

    private static void runPendingTeleport(PendingTeleport pending) {
        if (pending.player.hasDisconnected() || !pending.player.isAlive()) {
            return;
        }
        runWithServerTeleportBypass(pending.player, pending.action);
    }

    private static final class PendingTeleport {
        private final long requestId;
        private final ServerPlayer player;
        private final int source;
        private final Runnable action;
        private int age;

        private PendingTeleport(long requestId, ServerPlayer player, int source, Runnable action) {
            this.requestId = requestId;
            this.player = player;
            this.source = source;
            this.action = action;
        }
    }
}
