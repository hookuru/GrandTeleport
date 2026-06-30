package dev.codex.gtaliketeleport;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class TeleportTransitionController {
    private static final int PULL_TICKS = 40;
    private static final int PRE_TRAVEL_WAIT_TICKS = 20;
    private static final int TRAVEL_TICKS = 40;
    private static final int PRE_PUSH_WAIT_TICKS = 20;
    private static final int PUSH_TICKS = 40;
    private static final int TOTAL_TICKS = PULL_TICKS + PRE_TRAVEL_WAIT_TICKS + TRAVEL_TICKS + PRE_PUSH_WAIT_TICKS + PUSH_TICKS;
    private static final int POST_RELEASE_EFFECT_TICKS = 8;
    private static final int HUD_FADE_TICKS = 8;
    private static final boolean SEND_COMMAND_AT_TRAVEL_MIDPOINT = false;
    private static final double HIGH_ALTITUDE = 150.0D;
    private static final double MID_ALTITUDE = 50.0D;
    private static final double LOW_ALTITUDE = 10.0D;

    private static String pendingCommand;
    private static Vec3d startFeet;
    private static Vec3d plannedTargetFeet;
    private static Vec3d actualTargetFeet;
    private static Perspective previousPerspective;
    private static boolean previousHudHidden;
    private static float startYaw;
    private static int ticks;
    private static boolean commandSent;
    private static boolean cameraReleased;
    private static boolean hudSuppressed;

    private TeleportTransitionController() {
    }

    static void start(MinecraftClient client, String command) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        pendingCommand = command;
        startFeet = getFeetPos(player);
        plannedTargetFeet = TeleportDestinationParser.parse(command, player);
        actualTargetFeet = null;
        previousPerspective = client.options.getPerspective();
        previousHudHidden = client.options.hudHidden;
        startYaw = player.getYaw();
        ticks = 0;
        commandSent = false;
        cameraReleased = false;
        hudSuppressed = false;

        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    static void tick(MinecraftClient client) {
        if (!isRunning()) {
            return;
        }

        if (client.player == null || client.world == null || client.getNetworkHandler() == null) {
            clear(client);
            return;
        }

        ticks++;

        updateHudVisibility(client);

        if (isCameraStepSoundTick(ticks)) {
            playStepSound(client);
        }

        if (ticks == getTravelStartTick()) {
            playTravelSound(client);
        }

        if (!commandSent && ticks >= getCommandSendTick()) {
            commandSent = true;
            GtaLikeTeleportClient.sendDeferredCommand(pendingCommand);
        }

        if (commandSent && ticks > PULL_TICKS + 2) {
            Vec3d playerFeet = getFeetPos(client.player);
            if (actualTargetFeet == null && playerFeet.squaredDistanceTo(startFeet) > 4.0D) {
                actualTargetFeet = playerFeet;
            } else if (actualTargetFeet != null) {
                actualTargetFeet = playerFeet;
            }
        }

        if (!cameraReleased && ticks >= TOTAL_TICKS) {
            if (!commandSent) {
                commandSent = true;
                GtaLikeTeleportClient.sendDeferredCommand(pendingCommand);
            }

            cameraReleased = true;
            playStepSound(client);
            restorePerspective(client);
        }

        if (ticks >= TOTAL_TICKS + POST_RELEASE_EFFECT_TICKS) {
            clear(client);
        }
    }

    static boolean isRunning() {
        return pendingCommand != null;
    }

    static float getProgress() {
        if (!isRunning()) {
            return 0.0F;
        }

        return MathHelper.clamp(ticks / (float) TOTAL_TICKS, 0.0F, 1.0F);
    }

    static int getTicks() {
        return ticks;
    }

    public static boolean shouldBlockPlayerInput() {
        return isRunning() && !cameraReleased;
    }

    public static CameraFrame getCameraFrame(float tickProgress) {
        if (!isRunning()) {
            return null;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return null;
        }

        if (cameraReleased) {
            return null;
        }

        float frameTick = Math.min(ticks + tickProgress, TOTAL_TICKS);
        if (frameTick <= PULL_TICKS) {
            return pullFrame(frameTick / PULL_TICKS);
        }

        if (frameTick <= PULL_TICKS + PRE_TRAVEL_WAIT_TICKS) {
            return topDownFrame(startFeet, startYaw);
        }

        int travelStart = PULL_TICKS + PRE_TRAVEL_WAIT_TICKS;
        if (frameTick <= travelStart + TRAVEL_TICKS) {
            return travelFrame((frameTick - travelStart) / TRAVEL_TICKS);
        }

        int pushStart = travelStart + TRAVEL_TICKS + PRE_PUSH_WAIT_TICKS;
        if (frameTick <= pushStart) {
            return topDownFrame(getBestTargetFeet(), startYaw);
        }

        return pushFrame(player, (frameTick - pushStart) / PUSH_TICKS);
    }

    public static float getStepEffectIntensity(float tickProgress) {
        if (!isRunning()) {
            return 0.0F;
        }

        float frameTick = ticks + tickProgress;
        float intensity = 0.0F;
        intensity = Math.max(intensity, pulseAfter(frameTick, 0.0F));
        intensity = Math.max(intensity, pulseAfter(frameTick, PULL_TICKS / 3.0F));
        intensity = Math.max(intensity, pulseAfter(frameTick, PULL_TICKS * 2.0F / 3.0F));

        int pushStart = PULL_TICKS + PRE_TRAVEL_WAIT_TICKS + TRAVEL_TICKS + PRE_PUSH_WAIT_TICKS;
        intensity = Math.max(intensity, pulseAfter(frameTick, pushStart + PUSH_TICKS / 3.0F));
        intensity = Math.max(intensity, pulseAfter(frameTick, pushStart + PUSH_TICKS * 2.0F / 3.0F));
        intensity = Math.max(intensity, pulseAfter(frameTick, TOTAL_TICKS));
        return intensity;
    }

    public static float getHudFadeOverlayIntensity(float tickProgress) {
        if (!isRunning()) {
            return 0.0F;
        }

        float frameTick = ticks + tickProgress;
        float releaseTick = TOTAL_TICKS;

        if (frameTick < HUD_FADE_TICKS) {
            return smoothStep(frameTick / HUD_FADE_TICKS) * 0.46F;
        }

        if (frameTick < HUD_FADE_TICKS * 2.0F) {
            return (1.0F - smoothStep((frameTick - HUD_FADE_TICKS) / HUD_FADE_TICKS)) * 0.46F;
        }

        if (frameTick >= releaseTick && frameTick <= releaseTick + HUD_FADE_TICKS) {
            return (1.0F - smoothStep((frameTick - releaseTick) / HUD_FADE_TICKS)) * 0.46F;
        }

        return 0.0F;
    }

    private static CameraFrame pullFrame(float progress) {
        return new CameraFrame(startFeet.add(0.0D, pullAltitude(progress), 0.0D), startYaw, 90.0F);
    }

    private static CameraFrame topDownFrame(Vec3d feet, float yaw) {
        return new CameraFrame(feet.add(0.0D, HIGH_ALTITUDE, 0.0D), yaw, 90.0F);
    }

    private static CameraFrame travelFrame(float progress) {
        Vec3d source = startFeet.add(0.0D, HIGH_ALTITUDE, 0.0D);
        Vec3d target = getBestTargetFeet().add(0.0D, HIGH_ALTITUDE, 0.0D);
        Vec3d pos = source.lerp(target, cubicBezierEase(progress));

        return new CameraFrame(pos, startYaw, 90.0F);
    }

    private static CameraFrame pushFrame(ClientPlayerEntity player, float progress) {
        Vec3d playerFeet = getFeetPos(player);
        return new CameraFrame(playerFeet.add(0.0D, pushAltitude(progress), 0.0D), startYaw, 90.0F);
    }

    private static Vec3d getBestTargetFeet() {
        if (actualTargetFeet != null) {
            return actualTargetFeet;
        }

        if (plannedTargetFeet != null) {
            return plannedTargetFeet;
        }

        return startFeet;
    }

    private static double pullAltitude(float progress) {
        return steppedAltitude(progress, new double[]{LOW_ALTITUDE, MID_ALTITUDE, HIGH_ALTITUDE});
    }

    private static double pushAltitude(float progress) {
        return steppedAltitude(progress, new double[]{HIGH_ALTITUDE, MID_ALTITUDE, LOW_ALTITUDE});
    }

    private static double steppedAltitude(float progress, double[] levels) {
        int index = Math.min(levels.length - 1, (int) (MathHelper.clamp(progress, 0.0F, 0.9999F) * levels.length));
        return levels[index];
    }

    private static float pulseAfter(float tick, float startTick) {
        float age = tick - startTick;
        if (age < 0.0F || age > 7.0F) {
            return 0.0F;
        }

        float x = age / 7.0F;
        return (1.0F - x) * (1.0F - x);
    }

    private static float smoothStep(float value) {
        float x = MathHelper.clamp(value, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    private static boolean isCameraStepSoundTick(int tick) {
        int firstPullStep = 1;
        int secondPullStep = Math.round(PULL_TICKS / 3.0F);
        int thirdPullStep = Math.round(PULL_TICKS * 2.0F / 3.0F);
        int pushStart = PULL_TICKS + PRE_TRAVEL_WAIT_TICKS + TRAVEL_TICKS + PRE_PUSH_WAIT_TICKS;
        int secondPushStep = pushStart + Math.round(PUSH_TICKS / 3.0F);
        int thirdPushStep = pushStart + Math.round(PUSH_TICKS * 2.0F / 3.0F);

        return tick == firstPullStep
                || tick == secondPullStep
                || tick == thirdPullStep
                || tick == secondPushStep
                || tick == thirdPushStep;
    }

    private static void playStepSound(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.86F, 0.82F));
    }

    private static void playTravelSound(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRIGGER, 0.72F, 0.15F));
    }

    private static float cubicBezierEase(float progress) {
        float x = MathHelper.clamp(progress, 0.0F, 1.0F);
        float t = x;

        for (int i = 0; i < 5; i++) {
            float currentX = cubicBezier(t, 0.42F, 0.58F);
            float derivative = cubicBezierDerivative(t, 0.42F, 0.58F);
            if (Math.abs(derivative) < 0.0001F) {
                break;
            }

            t = MathHelper.clamp(t - (currentX - x) / derivative, 0.0F, 1.0F);
        }

        return cubicBezier(t, 0.0F, 1.0F);
    }

    private static float cubicBezier(float t, float control1, float control2) {
        float oneMinusT = 1.0F - t;
        return 3.0F * oneMinusT * oneMinusT * t * control1
                + 3.0F * oneMinusT * t * t * control2
                + t * t * t;
    }

    private static float cubicBezierDerivative(float t, float control1, float control2) {
        float oneMinusT = 1.0F - t;
        return 3.0F * oneMinusT * oneMinusT * control1
                + 6.0F * oneMinusT * t * (control2 - control1)
                + 3.0F * t * t * (1.0F - control2);
    }

    private static Vec3d getFeetPos(ClientPlayerEntity player) {
        return new Vec3d(player.getX(), player.getY(), player.getZ());
    }

    private static int getCommandSendTick() {
        if (SEND_COMMAND_AT_TRAVEL_MIDPOINT) {
            return PULL_TICKS + PRE_TRAVEL_WAIT_TICKS + TRAVEL_TICKS / 2;
        }

        return PULL_TICKS;
    }

    private static int getTravelStartTick() {
        return PULL_TICKS + PRE_TRAVEL_WAIT_TICKS;
    }

    private static void restorePerspective(MinecraftClient client) {
        if (previousPerspective != null) {
            client.options.setPerspective(previousPerspective);
        }
    }

    private static void updateHudVisibility(MinecraftClient client) {
        if (!hudSuppressed && ticks >= HUD_FADE_TICKS) {
            client.options.hudHidden = true;
            hudSuppressed = true;
        }

        if (hudSuppressed && cameraReleased) {
            client.options.hudHidden = previousHudHidden;
            hudSuppressed = false;
        }
    }

    private static void clear(MinecraftClient client) {
        restorePerspective(client);
        client.options.hudHidden = previousHudHidden;

        pendingCommand = null;
        startFeet = null;
        plannedTargetFeet = null;
        actualTargetFeet = null;
        previousPerspective = null;
        previousHudHidden = false;
        ticks = 0;
        commandSent = false;
        cameraReleased = false;
        hudSuppressed = false;
    }

    public record CameraFrame(Vec3d pos, float yaw, float pitch) {
    }
}






