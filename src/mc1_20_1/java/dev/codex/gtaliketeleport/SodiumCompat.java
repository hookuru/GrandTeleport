package dev.codex.gtaliketeleport;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

final class SodiumCompat {
    private static boolean active;

    private SodiumCompat() {
    }

    static void beginTransition(Minecraft client, boolean fallbackTerrainMode) {
        if (active) {
            return;
        }

        active = true;
        scheduleTerrainUpdate();
    }

    static void endTransition() {
        if (!active) {
            return;
        }

        active = false;
        scheduleTerrainUpdate();
    }


    static void scheduleTerrainUpdate() {
        try {
            Class<?> rendererClass = Class.forName("me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer");
            Method instanceNullable = rendererClass.getMethod("instanceNullable");
            Object renderer = instanceNullable.invoke(null);
            if (renderer == null) {
                return;
            }

            rendererClass.getMethod("scheduleTerrainUpdate").invoke(renderer);
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }
}
