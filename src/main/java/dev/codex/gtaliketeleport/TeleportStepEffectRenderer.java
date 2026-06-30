package dev.codex.gtaliketeleport;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class TeleportStepEffectRenderer {
    private TeleportStepEffectRenderer() {
    }

    public static void render(DrawContext context, float tickProgress) {
        float intensity = TeleportTransitionController.getStepEffectIntensity(tickProgress);
        intensity = Math.max(intensity, TeleportTransitionController.getHudFadeOverlayIntensity(tickProgress));
        if (intensity <= 0.0F) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int flashAlpha = (int) (68.0F * intensity);
        context.fill(0, 0, width, height, argb(flashAlpha, 245, 245, 235));
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return (clamp(alpha) << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
