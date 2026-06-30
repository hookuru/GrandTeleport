package dev.codex.gtaliketeleport;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

final class TeleportDestinationParser {
    private TeleportDestinationParser() {
    }

    static Vec3d parse(String command, ClientPlayerEntity player) {
        String argumentString = TeleportCommandMatcher.getArgumentString(command);
        if (argumentString == null || argumentString.isBlank()) {
            return null;
        }

        String[] args = argumentString.split("\\s+");
        if (args.length >= 3 && areCoordinateTriple(args, 0)) {
            return readCoordinateTriple(args, 0, player);
        }

        if (args.length >= 4 && areCoordinateTriple(args, 1)) {
            return readCoordinateTriple(args, 1, player);
        }

        for (int i = 0; i <= args.length - 3; i++) {
            if (areCoordinateTriple(args, i)) {
                return readCoordinateTriple(args, i, player);
            }
        }

        return null;
    }

    private static boolean areCoordinateTriple(String[] args, int start) {
        return isCoordinate(args[start]) && isCoordinate(args[start + 1]) && isCoordinate(args[start + 2]);
    }

    private static Vec3d readCoordinateTriple(String[] args, int start, ClientPlayerEntity player) {
        return new Vec3d(
                readCoordinate(args[start], player.getX()),
                readCoordinate(args[start + 1], player.getY()),
                readCoordinate(args[start + 2], player.getZ())
        );
    }

    private static boolean isCoordinate(String token) {
        if (token.isEmpty() || token.charAt(0) == '^') {
            return false;
        }

        if (token.charAt(0) == '~') {
            return token.length() == 1 || isDouble(token.substring(1));
        }

        return isDouble(token);
    }

    private static double readCoordinate(String token, double base) {
        if (token.charAt(0) == '~') {
            if (token.length() == 1) {
                return base;
            }

            return base + Double.parseDouble(token.substring(1));
        }

        return Double.parseDouble(token);
    }

    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
