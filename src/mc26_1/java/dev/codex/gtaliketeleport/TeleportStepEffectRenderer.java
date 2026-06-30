package dev.codex.gtaliketeleport;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector4f;

import java.lang.reflect.Field;

public final class TeleportStepEffectRenderer {
    private static final double DEFAULT_FOV_DEGREES = 70.0D;
    private static final double FALLBACK_MASK_CAMERA_HEIGHT = 150.0D;
    private static final double CHUNK_SCREEN_MARGIN = 32.0D;
    private static final int START_COLOR_CAPTURE_TICK = 30;
    private static final float SKY_TRANSITION_FOG_BLEND = 0.86F;
    private static Field fogRendererField;
    private static Field guiRenderStateField;
    private static boolean guiRenderStateUnavailable;
    private static int[] capturedStartMaskColor;

    private TeleportStepEffectRenderer() {
    }

    public static void render(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
        updateStartMaskColorCapture(deltaTracker);

        float tickProgress = deltaTracker.getGameTimeDeltaPartialTick(false);
        boolean shaderScreenMaskOnly = TeleportTransitionController.shouldUseShaderScreenMaskOnly();
        float maskIntensity = TeleportTransitionController.getShaderScreenMaskIntensity(tickProgress);
        if (maskIntensity > 0.0F) {
            renderChunkScreenMask(context, deltaTracker, tickProgress, maskIntensity);
        }

        float intensity;
        if (shaderScreenMaskOnly) {
            intensity = TeleportTransitionController.getCameraMotionStepEffectIntensity(tickProgress);
        } else {
            intensity = TeleportTransitionController.getStepEffectIntensity(tickProgress);
            intensity = Math.max(intensity, TeleportTransitionController.getHudFadeOverlayIntensity(tickProgress));
        }
        renderStepFlash(context, intensity);
    }

    private static void renderStepFlash(GuiGraphicsExtractor context, float intensity) {
        if (intensity <= 0.0F) {
            return;
        }

        int flashAlpha = (int) (68.0F * intensity);
        context.fill(0, 0, context.guiWidth(), context.guiHeight(), argb(flashAlpha, 245, 245, 235));
    }

    private static void updateStartMaskColorCapture(DeltaTracker deltaTracker) {
        if (!TeleportTransitionController.isRunning()) {
            capturedStartMaskColor = null;
            return;
        }

        if (capturedStartMaskColor != null
                || !TeleportTransitionController.shouldUseShaderScreenMaskOnly()
                || TeleportTransitionController.getTicks() < START_COLOR_CAPTURE_TICK) {
            return;
        }

        capturedStartMaskColor = getSkyMaskColor(deltaTracker);
    }

    private static int[] getSkyMaskColor(DeltaTracker deltaTracker) {
        int[] horizonColor = getRenderStateSkyMaskColor();
        if (horizonColor != null) {
            return horizonColor;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.gameRenderer != null) {
            try {
                Object value = getFogRendererField().get(client.gameRenderer);
                if (value instanceof FogRenderer fogRenderer) {
                    FogData fogData = fogRenderer.setupFog(
                            client.gameRenderer.getMainCamera(),
                            getFogViewDistance(client),
                            deltaTracker,
                            0.0F,
                            client.level
                    );
                    return softenMaskColor(fogData.color);
                }
            } catch (ReflectiveOperationException | LinkageError ignored) {
            }
        }

        return new int[]{188, 197, 202};
    }


    private static int[] getRenderStateSkyMaskColor() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.gameRenderer == null) {
            return null;
        }

        try {
            GameRenderState gameState = client.gameRenderer.getGameRenderState();
            LevelRenderState levelState = gameState.levelRenderState;
            int skyColor = levelState.skyRenderState.skyColor;
            int fogColor = skyColor;
            CameraRenderState cameraState = levelState.cameraRenderState;
            if (cameraState != null && cameraState.fogData != null) {
                fogColor = opaqueColor(cameraState.fogData.color);
            }

            float transitionAmount = ARGB.alphaFloat(levelState.skyRenderState.sunriseAndSunsetColor);
            int color = blendSkyTowardTransitionColor(skyColor, fogColor, transitionAmount);
            return neutralMaskColor(color);
        } catch (RuntimeException | LinkageError ignored) {
            return null;
        }
    }

    private static int blendSkyTowardTransitionColor(int skyColor, int fogColor, float transitionAmount) {
        if (transitionAmount <= 0.001F) {
            return skyColor;
        }

        int transitionBase = lessSaturatedColor(skyColor, fogColor);
        float blend = Mth.clamp(transitionAmount * SKY_TRANSITION_FOG_BLEND, 0.0F, SKY_TRANSITION_FOG_BLEND);
        return ARGB.srgbLerp(blend, skyColor, transitionBase);
    }

    private static int lessSaturatedColor(int first, int second) {
        return saturation(first) <= saturation(second) ? first : second;
    }

    private static int[] neutralMaskColor(int color) {
        int value = clamp(Math.round(ARGB.red(color) * 0.2126F + ARGB.green(color) * 0.7152F + ARGB.blue(color) * 0.0722F));
        return new int[]{value, value, value};
    }

    private static float saturation(int color) {
        float red = ARGB.redFloat(color);
        float green = ARGB.greenFloat(color);
        float blue = ARGB.blueFloat(color);
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        return max - min;
    }

    private static int opaqueColor(Vector4f color) {
        return ARGB.colorFromFloat(
                1.0F,
                Mth.clamp(color.x, 0.0F, 1.0F),
                Mth.clamp(color.y, 0.0F, 1.0F),
                Mth.clamp(color.z, 0.0F, 1.0F)
        );
    }
    private static void renderChunkScreenMask(GuiGraphicsExtractor context, DeltaTracker deltaTracker, float tickProgress, float maskIntensity) {
        TeleportTransitionController.CameraFrame frame = TeleportTransitionController.getCameraFrame(tickProgress);
        if (frame == null) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        double cameraY = FALLBACK_MASK_CAMERA_HEIGHT;
        double worldHalfHeight = Math.tan(Math.toRadians(DEFAULT_FOV_DEGREES) * 0.5D) * cameraY;
        double worldHalfWidth = worldHalfHeight * context.guiWidth() / Math.max(1.0D, context.guiHeight());
        double scaleX = context.guiWidth() / (worldHalfWidth * 2.0D);
        double scaleY = context.guiHeight() / (worldHalfHeight * 2.0D);
        double yawRadians = Math.toRadians(frame.yaw());
        double rightX = Math.cos(yawRadians);
        double rightZ = Math.sin(yawRadians);
        double forwardX = -Math.sin(yawRadians);
        double forwardZ = Math.cos(yawRadians);
        GuiRenderState guiRenderState = getGuiRenderState(context);

        double minWorldX = Double.POSITIVE_INFINITY;
        double maxWorldX = Double.NEGATIVE_INFINITY;
        double minWorldZ = Double.POSITIVE_INFINITY;
        double maxWorldZ = Double.NEGATIVE_INFINITY;
        double[] screenWorldXs = new double[]{
                -worldHalfWidth - CHUNK_SCREEN_MARGIN,
                worldHalfWidth + CHUNK_SCREEN_MARGIN,
                worldHalfWidth + CHUNK_SCREEN_MARGIN,
                -worldHalfWidth - CHUNK_SCREEN_MARGIN
        };
        double[] screenWorldYs = new double[]{
                -worldHalfHeight - CHUNK_SCREEN_MARGIN,
                -worldHalfHeight - CHUNK_SCREEN_MARGIN,
                worldHalfHeight + CHUNK_SCREEN_MARGIN,
                worldHalfHeight + CHUNK_SCREEN_MARGIN
        };

        for (int i = 0; i < 4; i++) {
            double worldX = frame.pos().x + rightX * screenWorldXs[i] + forwardX * screenWorldYs[i];
            double worldZ = frame.pos().z + rightZ * screenWorldXs[i] + forwardZ * screenWorldYs[i];
            minWorldX = Math.min(minWorldX, worldX);
            maxWorldX = Math.max(maxWorldX, worldX);
            minWorldZ = Math.min(minWorldZ, worldZ);
            maxWorldZ = Math.max(maxWorldZ, worldZ);
        }

        int minChunkX = Mth.floor(minWorldX / 16.0D) - 1;
        int maxChunkX = Mth.floor(maxWorldX / 16.0D) + 1;
        int minChunkZ = Mth.floor(minWorldZ / 16.0D) - 1;
        int maxChunkZ = Mth.floor(maxWorldZ / 16.0D) + 1;
        int[] color = getSceneMaskColor(deltaTracker);
        boolean shaderScreenMaskOnly = TeleportTransitionController.shouldUseShaderScreenMaskOnly();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                double chunkMinWorldX = chunkX * 16.0D;
                double chunkMinWorldZ = chunkZ * 16.0D;
                float sectionOpacity;
                if (shaderScreenMaskOnly) {
                    sectionOpacity = TeleportTransitionController.getShaderScreenMaskSectionOpacity(
                            chunkMinWorldX + 8.0D,
                            frame.pos().y,
                            chunkMinWorldZ + 8.0D
                    );
                } else {
                    float visibility = TeleportTransitionController.getFallbackTerrainSectionVisibility(
                            chunkMinWorldX + 8.0D,
                            frame.pos().y,
                            chunkMinWorldZ + 8.0D
                    );
                    sectionOpacity = 1.0F - visibility;
                }
                float opacity = sectionOpacity * maskIntensity;
                if (opacity <= 0.001F) {
                    continue;
                }

                int alpha = (int) (252.0F * opacity);
                drawProjectedChunkQuad(
                        context,
                        guiRenderState,
                        frame.pos(),
                        chunkMinWorldX,
                        chunkMinWorldZ,
                        rightX,
                        rightZ,
                        forwardX,
                        forwardZ,
                        scaleX,
                        scaleY,
                        alpha,
                        color
                );
            }
        }
    }

    private static void drawProjectedChunkQuad(
            GuiGraphicsExtractor context,
            GuiRenderState guiRenderState,
            Vec3 cameraPos,
            double minWorldX,
            double minWorldZ,
            double rightX,
            double rightZ,
            double forwardX,
            double forwardZ,
            double scaleX,
            double scaleY,
            int alpha,
            int[] color
    ) {
        double maxWorldX = minWorldX + 16.0D;
        double maxWorldZ = minWorldZ + 16.0D;
        double[] worldXs = new double[]{minWorldX, maxWorldX, maxWorldX, minWorldX};
        double[] worldZs = new double[]{minWorldZ, minWorldZ, maxWorldZ, maxWorldZ};
        double[] screenXs = new double[4];
        double[] screenYs = new double[4];

        for (int i = 0; i < 4; i++) {
            double dx = worldXs[i] - cameraPos.x;
            double dz = worldZs[i] - cameraPos.z;
            double screenWorldX = dx * rightX + dz * rightZ;
            double screenWorldY = dx * forwardX + dz * forwardZ;
            screenXs[i] = context.guiWidth() * 0.5D + screenWorldX * scaleX;
            screenYs[i] = context.guiHeight() * 0.5D + screenWorldY * scaleY;
        }

        ScreenRectangle bounds = getScreenBounds(screenXs, screenYs, context.guiWidth(), context.guiHeight());
        if (bounds == null) {
            return;
        }

        if (guiRenderState != null) {
            int packedColor = argb(alpha, color[0], color[1], color[2]);
            guiRenderState.addGuiElement(new ProjectedQuadRenderState(
                    RenderPipelines.GUI,
                    TextureSetup.noTexture(),
                    new Matrix3x2f(context.pose()),
                    (float) screenXs[0],
                    (float) screenYs[0],
                    (float) screenXs[1],
                    (float) screenYs[1],
                    (float) screenXs[2],
                    (float) screenYs[2],
                    (float) screenXs[3],
                    (float) screenYs[3],
                    packedColor,
                    null,
                    bounds
            ));
            return;
        }

        drawProjectedChunkScanlines(context, screenXs, screenYs, alpha, color);
    }

    private static ScreenRectangle getScreenBounds(double[] xs, double[] ys, int screenWidth, int screenHeight) {
        double minScreenX = Double.POSITIVE_INFINITY;
        double maxScreenX = Double.NEGATIVE_INFINITY;
        double minScreenY = Double.POSITIVE_INFINITY;
        double maxScreenY = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            minScreenX = Math.min(minScreenX, xs[i]);
            maxScreenX = Math.max(maxScreenX, xs[i]);
            minScreenY = Math.min(minScreenY, ys[i]);
            maxScreenY = Math.max(maxScreenY, ys[i]);
        }

        int x1 = Math.max(0, Mth.floor(minScreenX));
        int y1 = Math.max(0, Mth.floor(minScreenY));
        int x2 = Math.min(screenWidth, Mth.ceil(maxScreenX));
        int y2 = Math.min(screenHeight, Mth.ceil(maxScreenY));
        if (x2 <= x1 || y2 <= y1) {
            return null;
        }

        return new ScreenRectangle(x1, y1, x2 - x1, y2 - y1);
    }

    private static void drawProjectedChunkScanlines(
            GuiGraphicsExtractor context,
            double[] xs,
            double[] ys,
            int alpha,
            int[] color
    ) {
        double minScreenY = Math.min(Math.min(ys[0], ys[1]), Math.min(ys[2], ys[3]));
        double maxScreenY = Math.max(Math.max(ys[0], ys[1]), Math.max(ys[2], ys[3]));
        int y1 = Math.max(0, Mth.floor(minScreenY));
        int y2 = Math.min(context.guiHeight(), Mth.ceil(maxScreenY));
        if (y2 <= y1) {
            return;
        }

        int fillColor = argb(alpha, color[0], color[1], color[2]);
        for (int y = y1; y < y2; y += 2) {
            double scanY = Math.min(y2 - 0.5D, y + 1.0D);
            double[] intersections = new double[4];
            int count = 0;
            for (int i = 0; i < 4; i++) {
                int next = (i + 1) & 3;
                double edgeY1 = ys[i];
                double edgeY2 = ys[next];
                if ((edgeY1 <= scanY && edgeY2 > scanY) || (edgeY2 <= scanY && edgeY1 > scanY)) {
                    double t = (scanY - edgeY1) / (edgeY2 - edgeY1);
                    intersections[count++] = xs[i] + (xs[next] - xs[i]) * t;
                }
            }

            if (count < 2) {
                continue;
            }

            double left = Math.min(intersections[0], intersections[1]);
            double right = Math.max(intersections[0], intersections[1]);
            int x1 = Math.max(0, Mth.floor(left));
            int x2 = Math.min(context.guiWidth(), Mth.ceil(right));
            if (x2 > x1) {
                context.fill(x1, y, x2, Math.min(y + 2, y2), fillColor);
            }
        }
    }

    private static Field getFogRendererField() throws NoSuchFieldException {
        if (fogRendererField == null) {
            fogRendererField = net.minecraft.client.renderer.GameRenderer.class.getDeclaredField("fogRenderer");
            fogRendererField.setAccessible(true);
        }

        return fogRendererField;
    }

    private static GuiRenderState getGuiRenderState(GuiGraphicsExtractor context) {
        if (guiRenderStateUnavailable) {
            return null;
        }

        try {
            Object value = getGuiRenderStateField().get(context);
            if (value instanceof GuiRenderState guiRenderState) {
                return guiRenderState;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            guiRenderStateUnavailable = true;
        }

        return null;
    }

    private static Field getGuiRenderStateField() throws NoSuchFieldException {
        if (guiRenderStateField == null) {
            guiRenderStateField = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
            guiRenderStateField.setAccessible(true);
        }

        return guiRenderStateField;
    }

    private static int getFogViewDistance(Minecraft client) {
        return Math.max(2, client.options == null ? 12 : client.options.getEffectiveRenderDistance());
    }

    private static int[] getSceneMaskColor(DeltaTracker deltaTracker) {
        if (capturedStartMaskColor == null) {
            return getSkyMaskColor(deltaTracker);
        }

        return capturedStartMaskColor;
    }

    private static int[] softenMaskColor(int color) {
        return softenMaskColor(new Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), 1.0F));
    }

    private static int[] softenMaskColor(Vector4f color) {
        float red = Mth.clamp(color.x, 0.0F, 1.0F);
        float green = Mth.clamp(color.y, 0.0F, 1.0F);
        float blue = Mth.clamp(color.z, 0.0F, 1.0F);
        float luma = red * 0.2126F + green * 0.7152F + blue * 0.0722F;
        red = red * 0.62F + luma * 0.38F;
        green = green * 0.62F + luma * 0.38F;
        blue = blue * 0.62F + luma * 0.38F;
        red = red * 0.78F + 0.20F;
        green = green * 0.78F + 0.20F;
        blue = blue * 0.78F + 0.20F;
        return new int[]{
                (int) (Mth.clamp(red, 0.0F, 1.0F) * 255.0F),
                (int) (Mth.clamp(green, 0.0F, 1.0F) * 255.0F),
                (int) (Mth.clamp(blue, 0.0F, 1.0F) * 255.0F)
        };
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return (clamp(alpha) << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private record ProjectedQuadRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2fc pose,
            float x0,
            float y0,
            float x1,
            float y1,
            float x2,
            float y2,
            float x3,
            float y3,
            int color,
            ScreenRectangle scissorArea,
            ScreenRectangle bounds
    ) implements GuiElementRenderState {
        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {
            vertexConsumer.addVertexWith2DPose(pose, x0, y0).setColor(color);
            vertexConsumer.addVertexWith2DPose(pose, x3, y3).setColor(color);
            vertexConsumer.addVertexWith2DPose(pose, x2, y2).setColor(color);
            vertexConsumer.addVertexWith2DPose(pose, x1, y1).setColor(color);
        }
    }
}
