package dev.codex.gtaliketeleport;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class GtaLikeTeleportClient implements ClientModInitializer {
    private static final String[] COMMAND_ALIASES = {"grandtp", "gtp"};
    private static final String USAGE_MESSAGE = "Usage: /gtp or /grandtp on|off|status";

    private static boolean bypassNextCommand;

    @Override
    public void onInitializeClient() {
        GtaLikeTeleportConfig.load();
        registerGtaTeleportCommand();

        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {
            if (bypassNextCommand) {
                return true;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (handleGtaTeleportCommand(client, command)) {
                return false;
            }

            if (!GtaLikeTeleportConfig.isEffectEnabled()) {
                return true;
            }

            if (!TeleportCommandMatcher.isTeleportCommand(command) || client.player == null || client.getNetworkHandler() == null) {
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
        });

        ClientTickEvents.END_CLIENT_TICK.register(TeleportTransitionController::tick);
    }

    static void sendDeferredCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) {
            return;
        }

        bypassNextCommand = true;

        try {
            client.getNetworkHandler().sendChatCommand(command);
        } finally {
            bypassNextCommand = false;
        }
    }

    private static void registerGtaTeleportCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String commandName : COMMAND_ALIASES) {
                dispatcher.register(literal(commandName)
                        .executes(context -> sendStatusFeedback(context.getSource()))
                        .then(literal("on").executes(context -> setEffectEnabled(context.getSource(), true)))
                        .then(literal("off").executes(context -> setEffectEnabled(context.getSource(), false)))
                        .then(literal("status").executes(context -> sendStatusFeedback(context.getSource())))
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

        source.sendError(Text.literal(USAGE_MESSAGE));
        return 0;
    }

    private static int setEffectEnabled(FabricClientCommandSource source, boolean enabled) {
        boolean saved = GtaLikeTeleportConfig.setEffectEnabled(enabled);
        source.sendFeedback(createStateFeedback(enabled, saved, saved ? Formatting.GREEN : Formatting.YELLOW));
        return 1;
    }

    private static int sendStatusFeedback(FabricClientCommandSource source) {
        source.sendFeedback(createStateFeedback(GtaLikeTeleportConfig.isEffectEnabled(), true, Formatting.GRAY));
        return 1;
    }

    private static boolean canExecuteServerCommand(MinecraftClient client, String command) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            return false;
        }

        String normalized = normalizeCommand(command);
        if (normalized.isEmpty()) {
            return false;
        }

        ParseResults<ClientCommandSource> parseResults = networkHandler.getCommandDispatcher().parse(
                normalized,
                networkHandler.getCommandSource()
        );
        return !parseResults.getReader().canRead() && hasExecutableCommand(parseResults.getContext());
    }

    private static boolean hasExecutableCommand(CommandContextBuilder<ClientCommandSource> context) {
        CommandContextBuilder<ClientCommandSource> current = context;
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

    private static boolean handleGtaTeleportCommand(MinecraftClient client, String command) {
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
            sendFeedback(client, createStateFeedback(GtaLikeTeleportConfig.isEffectEnabled(), true, Formatting.GRAY));
            return true;
        }

        sendFeedback(client, Text.literal(USAGE_MESSAGE).formatted(Formatting.RED));
        return true;
    }

    private static void sendCommandFeedback(MinecraftClient client, boolean enabled, boolean saved) {
        sendFeedback(client, createStateFeedback(enabled, saved, saved ? Formatting.GREEN : Formatting.YELLOW));
    }

    private static Text createStateFeedback(boolean enabled, boolean saved, Formatting formatting) {
        String state = enabled ? "ON" : "OFF";
        String message = "Grand Teleport:" + state + (saved ? "" : " (save failed)");
        return Text.literal(message).formatted(formatting);
    }

    private static void sendFeedback(MinecraftClient client, Text message) {
        if (client.player != null) {
            client.player.sendMessage(message, false);
        }
    }
}
