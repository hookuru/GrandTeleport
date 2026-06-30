package dev.codex.gtaliketeleport;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class GtaLikeTeleportClient implements ClientModInitializer {
    private static final String[] COMMAND_ALIASES = {"grandtp", "gtp"};
    private static final String USAGE_MESSAGE = "Usage: /gtp or /grandtp on|off|status|player_freeze <on|off|status>";

    private static boolean bypassNextCommand;
    private static boolean bypassNextPacket;
    private static boolean bypassNextJourneyMapTeleport;

    @Override
    public void onInitializeClient() {
        GtaLikeTeleportConfig.load();
        GtaLikeTeleportNetworkPayloads.register();
        GtaLikeTeleportClientNetworking.registerReceivers();
        registerGtaTeleportCommand();

        ClientSendMessageEvents.ALLOW_COMMAND.register(GtaLikeTeleportClient::interceptOutgoingCommand);

        ClientTickEvents.END_CLIENT_TICK.register(TeleportTransitionController::tick);
    }

    public static boolean interceptOutgoingCommand(String command) {
        if (bypassNextCommand) {
            return true;
        }

        Minecraft client = Minecraft.getInstance();
        if (handleGtaTeleportCommand(client, command)) {
            return false;
        }

        if (!GtaLikeTeleportConfig.isEffectEnabled()) {
            return true;
        }

        if (!TeleportCommandMatcher.isTeleportCommand(command) || client.player == null || client.getConnection() == null) {
            return true;
        }

        if (!canExecuteServerCommand(client, command)) {
            return true;
        }

        if (TeleportTransitionController.isRunning()) {
            return true;
        }

        TeleportTransitionController.start(client, command);
        return false;
    }

    public static boolean interceptOutgoingPacket(Connection connection, Packet<?> packet, PacketSendListener listener) {
        if (bypassNextPacket) {
            return true;
        }

        PacketTeleportTarget teleportTarget = getTeleportPacketTarget(packet);
        if (teleportTarget == null) {
            return true;
        }

        Minecraft client = Minecraft.getInstance();
        if (!GtaLikeTeleportConfig.isEffectEnabled() || client.player == null || client.level == null || client.getConnection() == null) {
            return true;
        }

        if (TeleportTransitionController.isRunning()) {
            return true;
        }

        TeleportTransitionController.start(
                client,
                teleportTarget.targetFeet(),
                teleportTarget.targetDimensionId(),
                () -> sendDeferredPacket(connection, packet, listener),
                !teleportTarget.keepMenuOpen()
        );
        return false;
    }

    public static boolean interceptJourneyMapTeleport(Vec3 targetFeet, Runnable action) {
        return interceptJourneyMapTeleport(targetFeet, null, action);
    }

    public static boolean interceptJourneyMapTeleport(Vec3 targetFeet, String targetDimensionId, Runnable action) {
        if (bypassNextJourneyMapTeleport) {
            return true;
        }

        Minecraft client = Minecraft.getInstance();
        if (!GtaLikeTeleportConfig.isEffectEnabled() || client.player == null || client.level == null || client.getConnection() == null) {
            return true;
        }

        if (TeleportTransitionController.isRunning()) {
            return true;
        }

        TeleportTransitionController.start(client, targetFeet, targetDimensionId, () -> sendDeferredJourneyMapTeleport(action));
        return false;
    }



    static void handleServerTeleportRequest(GtaLikeTeleportNetworkPayloads.StartServerTeleportPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (!shouldPlayServerTeleportTransition(client, payload.source())) {
            GtaLikeTeleportClientNetworking.sendServerTeleportAck(payload.requestId());
            return;
        }

        TeleportTransitionController.start(
                client,
                GtaLikeTeleportClientNetworking.targetFeet(payload),
                GtaLikeTeleportClientNetworking.targetDimensionId(payload),
                () -> GtaLikeTeleportClientNetworking.sendServerTeleportAck(payload.requestId())
        );
    }

    private static boolean shouldPlayServerTeleportTransition(Minecraft client, int source) {
        if (!GtaLikeTeleportConfig.isEffectEnabled() || client.player == null || client.level == null || client.getConnection() == null) {
            return false;
        }
        if (TeleportTransitionController.isRunning()) {
            return false;
        }
        if (source == GtaLikeTeleportNetworkPayloads.SOURCE_WARP_PLATE) {
            return GtaLikeTeleportConfig.isWarpPlateTransitionsEnabled();
        }
        return GtaLikeTeleportConfig.isExternalTeleportTransitionsEnabled();
    }
    static void sendDeferredCommand(String command) {
        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) {
            return;
        }

        GtaLikeTeleportClientNetworking.sendBypassNextServerTeleport();
        bypassNextCommand = true;

        try {
            client.getConnection().sendCommand(command);
        } finally {
            bypassNextCommand = false;
        }
    }

    private static void sendDeferredPacket(Connection connection, Packet<?> packet, PacketSendListener listener) {
        GtaLikeTeleportClientNetworking.sendBypassNextServerTeleport();
        bypassNextPacket = true;

        try {
            if (listener == null) {
                connection.send(packet);
            } else {
                connection.send(packet, listener);
            }
        } finally {
            bypassNextPacket = false;
        }
    }

    private static void sendDeferredJourneyMapTeleport(Runnable action) {
        GtaLikeTeleportClientNetworking.sendBypassNextServerTeleport();
        bypassNextJourneyMapTeleport = true;

        try {
            action.run();
        } finally {
            bypassNextJourneyMapTeleport = false;
        }
    }


    private static PacketTeleportTarget getTeleportPacketTarget(Packet<?> packet) {
        if (!(packet instanceof ServerboundCustomPayloadPacket customPayloadPacket)) {
            return null;
        }

        ResourceLocation id = customPayloadPacket.getIdentifier();
        FriendlyByteBuf data = new FriendlyByteBuf(customPayloadPacket.getData().copy());
        try {
            PacketTeleportTarget journeyMapTarget = getJourneyMapTeleportTarget(data, id);
            if (journeyMapTarget != null) {
                return journeyMapTarget;
            }

            data.readerIndex(0);
            return getWaystonesTeleportTarget(data, id);
        } finally {
            data.release();
        }
    }

    private static PacketTeleportTarget getJourneyMapTeleportTarget(FriendlyByteBuf payload, ResourceLocation id) {
        if (!id.getNamespace().equals("journeymap") || !id.getPath().equals("teleport_req")) {
            return null;
        }

        try {
            Vec3 targetFeet = new Vec3(payload.readDouble(), payload.readDouble(), payload.readDouble());
            String dimension = payload.isReadable() ? DimensionIds.normalize(payload.readUtf(32767)) : null;
            return new PacketTeleportTarget(targetFeet, dimension, false);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static PacketTeleportTarget getWaystonesTeleportTarget(FriendlyByteBuf payload, ResourceLocation id) {
        if (!id.getNamespace().equals("waystones")) {
            return null;
        }

        if (id.getPath().equals("select_waystone")) {
            WaystoneTarget target = getWaystonesSelectedTarget(payload);
            return target == null ? null : new PacketTeleportTarget(target.targetFeet(), target.targetDimensionId(), true);
        }

        if (id.getPath().equals("inventory_button")) {
            WaystoneTarget target = getWaystonesInventoryButtonTarget();
            return target == null ? null : new PacketTeleportTarget(target.targetFeet(), target.targetDimensionId(), false);
        }

        return null;
    }

    private static WaystoneTarget getWaystonesSelectedTarget(FriendlyByteBuf payload) {
        try {
            UUID waystoneUid = payload.readUUID();
            Minecraft client = Minecraft.getInstance();
            Object menu = client.player == null ? null : client.player.containerMenu;
            WaystoneTarget menuTarget = findWaystoneTargetInMenu(menu, waystoneUid);
            return menuTarget != null ? menuTarget : findWaystoneTargetInStore(waystoneUid);
        } catch (RuntimeException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException ignored) {
            return null;
        }
    }

    private static WaystoneTarget getWaystonesInventoryButtonTarget() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return null;
        }

        try {
            Class<?> managerClass = Class.forName("net.blay09.mods.waystones.core.PlayerWaystoneManager");
            Method method = managerClass.getMethod("getInventoryButtonTarget", Player.class);
            Object result = method.invoke(null, client.player);
            if (!(result instanceof Optional<?> optional) || optional.isEmpty()) {
                return null;
            }

            return getWaystoneTarget(optional.get());
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException ignored) {
            return null;
        }
    }

    private static WaystoneTarget findWaystoneTargetInMenu(Object menu, UUID waystoneUid)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (menu == null) {
            return null;
        }

        Method method = menu.getClass().getMethod("getWaystones");
        Object result = method.invoke(menu);
        if (!(result instanceof Collection<?> waystones)) {
            return null;
        }

        for (Object waystone : waystones) {
            if (waystoneUid.equals(readUuid(waystone, "getWaystoneUid"))) {
                return getWaystoneTarget(waystone);
            }
        }

        return null;
    }

    private static WaystoneTarget findWaystoneTargetInStore(UUID waystoneUid)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clientClass = Class.forName("net.blay09.mods.waystones.client.WaystonesClient");
        Object store = clientClass.getMethod("getWaystonesStore").invoke(null);
        Object result = store.getClass().getMethod("getWaystoneById", UUID.class).invoke(store, waystoneUid);
        if (!(result instanceof Optional<?> optional) || optional.isEmpty()) {
            return null;
        }

        return getWaystoneTarget(optional.get());
    }

    private static WaystoneTarget getWaystoneTarget(Object waystone)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object result = waystone.getClass().getMethod("getPos").invoke(waystone);
        if (!(result instanceof BlockPos pos)) {
            return null;
        }

        Vec3 targetFeet = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        return new WaystoneTarget(targetFeet, readOptionalDimensionId(waystone, "getDimension"));
    }
    private static UUID readUuid(Object target, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = target.getClass().getMethod(methodName);
        return (UUID) method.invoke(target);
    }

    private static double readDouble(Object target, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = target.getClass().getMethod(methodName);
        return ((Number) method.invoke(target)).doubleValue();
    }

    private static String readOptionalDimensionId(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object result = method.invoke(target);
            if (result instanceof ResourceKey<?> key) {
                return DimensionIds.fromResourceKey(key);
            }
            return DimensionIds.normalize(result == null ? null : result.toString());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException ignored) {
            return null;
        }
    }

    private record WaystoneTarget(Vec3 targetFeet, String targetDimensionId) {
    }

    private record PacketTeleportTarget(Vec3 targetFeet, String targetDimensionId, boolean keepMenuOpen) {
    }

    private static void registerGtaTeleportCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String commandName : COMMAND_ALIASES) {
                dispatcher.register(literal(commandName)
                        .executes(context -> sendStatusFeedback(context.getSource()))
                        .then(literal("on").executes(context -> setEffectEnabled(context.getSource(), true)))
                        .then(literal("off").executes(context -> setEffectEnabled(context.getSource(), false)))
                        .then(literal("status").executes(context -> sendStatusFeedback(context.getSource())))
                        .then(literal("player_freeze")
                                .executes(context -> sendPlayerFreezeStatusFeedback(context.getSource()))
                                .then(literal("on").executes(context -> setPlayerFreezeEnabled(context.getSource(), true)))
                                .then(literal("off").executes(context -> setPlayerFreezeEnabled(context.getSource(), false)))
                                .then(literal("status").executes(context -> sendPlayerFreezeStatusFeedback(context.getSource()))))
                        .then(argument("value", StringArgumentType.word()).executes(context -> handleCommandArgument(
                                context.getSource(),
                                StringArgumentType.getString(context, "value")
                        )))
                );
            }
        });
    }

    private static int handleCommandArgument(FabricClientCommandSource source, String argument) {
        String lowerArgument = argument.toLowerCase(Locale.ROOT);
        if (lowerArgument.equals("on")) {
            return setEffectEnabled(source, true);
        }

        if (lowerArgument.equals("off")) {
            return setEffectEnabled(source, false);
        }

        if (lowerArgument.equals("status")) {
            return sendStatusFeedback(source);
        }

        if (lowerArgument.equals("player_freeze")) {
            return sendPlayerFreezeStatusFeedback(source);
        }

        source.sendError(Component.literal(USAGE_MESSAGE));
        return 0;
    }

    private static int setEffectEnabled(FabricClientCommandSource source, boolean enabled) {
        boolean saved = GtaLikeTeleportConfig.setEffectEnabled(enabled);
        source.sendFeedback(createStateFeedback(enabled, saved, saved ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        return 1;
    }

    private static int sendStatusFeedback(FabricClientCommandSource source) {
        source.sendFeedback(createStateFeedback(GtaLikeTeleportConfig.isEffectEnabled(), true, ChatFormatting.GRAY));
        return 1;
    }

    private static int setPlayerFreezeEnabled(FabricClientCommandSource source, boolean enabled) {
        boolean saved = GtaLikeTeleportConfig.setPlayerFreezeEnabled(enabled);
        source.sendFeedback(createPlayerFreezeStateFeedback(enabled, saved, saved ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        return 1;
    }

    private static int sendPlayerFreezeStatusFeedback(FabricClientCommandSource source) {
        source.sendFeedback(createPlayerFreezeStateFeedback(
                GtaLikeTeleportConfig.isPlayerFreezeEnabled(),
                true,
                ChatFormatting.GRAY
        ));
        return 1;
    }

    private static boolean canExecuteServerCommand(Minecraft client, String command) {
        ClientPacketListener networkHandler = client.getConnection();
        if (networkHandler == null) {
            return false;
        }

        String normalized = normalizeCommand(command);
        if (normalized.isEmpty()) {
            return false;
        }

        ParseResults<?> parseResults = networkHandler.getCommands().parse(
                normalized,
                networkHandler.getSuggestionsProvider()
        );
        return !parseResults.getReader().canRead() && hasExecutableCommand(parseResults.getContext());
    }

    private static boolean hasExecutableCommand(CommandContextBuilder<?> context) {
        CommandContextBuilder<?> current = context;
        while (current != null) {
            if (current.getCommand() != null) {
                return true;
            }

            current = current.getChild();
        }

        return false;
    }

    private static String normalizeCommand(String command) {
        String normalized = command.strip();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1).stripLeading();
        }

        return normalized;
    }

    private static String getLocalCommandName(String normalized) {
        int end = 0;
        while (end < normalized.length() && !Character.isWhitespace(normalized.charAt(end))) {
            end++;
        }

        String commandName = normalized.substring(0, end).toLowerCase(Locale.ROOT);
        for (String alias : COMMAND_ALIASES) {
            if (commandName.equals(alias)) {
                return normalized.substring(0, end);
            }
        }

        return null;
    }

    private static boolean handleGtaTeleportCommand(Minecraft client, String command) {
        String normalized = command.stripLeading();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1).stripLeading();
        }

        String commandName = getLocalCommandName(normalized);
        if (commandName == null) {
            return false;
        }

        String argument = normalized.length() == commandName.length()
                ? ""
                : normalized.substring(commandName.length()).strip();
        String lowerArgument = argument.toLowerCase(Locale.ROOT);

        if (lowerArgument.equals("on")) {
            boolean saved = GtaLikeTeleportConfig.setEffectEnabled(true);
            sendCommandFeedback(client, true, saved);
            return true;
        }

        if (lowerArgument.equals("off")) {
            boolean saved = GtaLikeTeleportConfig.setEffectEnabled(false);
            sendCommandFeedback(client, false, saved);
            return true;
        }

        if (lowerArgument.equals("status") || lowerArgument.isEmpty()) {
            sendFeedback(client, createStateFeedback(GtaLikeTeleportConfig.isEffectEnabled(), true, ChatFormatting.GRAY));
            return true;
        }

        if (lowerArgument.equals("player_freeze") || lowerArgument.equals("player_freeze status")) {
            sendFeedback(client, createPlayerFreezeStateFeedback(
                    GtaLikeTeleportConfig.isPlayerFreezeEnabled(),
                    true,
                    ChatFormatting.GRAY
            ));
            return true;
        }

        if (lowerArgument.equals("player_freeze on")) {
            boolean saved = GtaLikeTeleportConfig.setPlayerFreezeEnabled(true);
            sendFeedback(client, createPlayerFreezeStateFeedback(true, saved, saved ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
            return true;
        }

        if (lowerArgument.equals("player_freeze off")) {
            boolean saved = GtaLikeTeleportConfig.setPlayerFreezeEnabled(false);
            sendFeedback(client, createPlayerFreezeStateFeedback(false, saved, saved ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
            return true;
        }

        sendFeedback(client, Component.literal(USAGE_MESSAGE).withStyle(ChatFormatting.RED));
        return true;
    }

    private static void sendCommandFeedback(Minecraft client, boolean enabled, boolean saved) {
        sendFeedback(client, createStateFeedback(enabled, saved, saved ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }

    private static Component createStateFeedback(boolean enabled, boolean saved, ChatFormatting formatting) {
        String state = enabled ? "ON" : "OFF";
        String message = "Grand Teleport:" + state + (saved ? "" : " (save failed)");
        return Component.literal(message).withStyle(formatting);
    }

    private static Component createPlayerFreezeStateFeedback(boolean enabled, boolean saved, ChatFormatting formatting) {
        String state = enabled ? "ON" : "OFF";
        String message = "Grand Teleport player_freeze:" + state + (saved ? "" : " (save failed)");
        return Component.literal(message).withStyle(formatting);
    }

    private static void sendFeedback(Minecraft client, Component message) {
        if (client.player != null) {
            client.player.sendSystemMessage(message);
        }
    }
}
