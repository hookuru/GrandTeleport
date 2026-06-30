package dev.codex.gtaliketeleport;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

final class SodiumCompat {
    private static final double FALLBACK_CHUNK_SECTION_FADE_SECONDS = 0.75D;
    private static boolean active;

    private SodiumCompat() {
    }

    static void beginTransition(Minecraft client, boolean fallbackTerrainMode) {
        ensureFallbackChunkFadeInTime(client, fallbackTerrainMode);
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

    static void ensureFallbackChunkFadeInTime(Minecraft client, boolean fallbackTerrainMode) {
        if (!fallbackTerrainMode || client == null || client.options == null) {
            return;
        }

        Double seconds = client.options.chunkSectionFadeInTime().get();
        if (seconds == null || seconds < FALLBACK_CHUNK_SECTION_FADE_SECONDS) {
            client.options.chunkSectionFadeInTime().set(FALLBACK_CHUNK_SECTION_FADE_SECONDS);
        }
    }

    static void scheduleTerrainUpdate() {
        try {
            Class<?> rendererClass = Class.forName("net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer");
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