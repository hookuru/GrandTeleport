package dev.codex.gtaliketeleport;

import net.minecraft.resources.ResourceKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

final class DimensionIds {
    static final String OVERWORLD = "minecraft:overworld";
    static final String NETHER = "minecraft:the_nether";
    static final String END = "minecraft:the_end";

    private DimensionIds() {
    }

    static String fromResourceKey(ResourceKey<?> key) {
        if (key == null) {
            return null;
        }

        for (String methodName : new String[]{"location", "getValue", "getLocation"}) {
            try {
                Method method = key.getClass().getMethod(methodName);
                Object result = method.invoke(key);
                String normalized = normalize(result == null ? null : result.toString());
                if (normalized != null) {
                    return normalized;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }

        return normalize(key.toString());
    }

    static String normalize(String raw) {
        if (raw == null) {
            return null;
        }

        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return null;
        }

        if (value.contains(OVERWORLD)) {
            return OVERWORLD;
        }
        if (value.contains(NETHER)) {
            return NETHER;
        }
        if (value.contains(END)) {
            return END;
        }

        return switch (value) {
            case "overworld", "minecraft:overworld" -> OVERWORLD;
            case "nether", "the_nether", "minecraft:nether", "minecraft:the_nether" -> NETHER;
            case "end", "the_end", "minecraft:end", "minecraft:the_end" -> END;
            default -> value;
        };
    }
}
