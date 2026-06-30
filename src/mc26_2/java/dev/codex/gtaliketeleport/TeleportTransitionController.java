package dev.codex.gtaliketeleport;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;

public final class TeleportTransitionController {
    private static final int EXIT_BODY_TICKS = 12;

    private static final int PRE_TRAVEL_WAIT_TICKS = 10;
    private static final int DEFAULT_TRAVEL_TICKS = 40;
    private static final int MIN_TRAVEL_TICKS = 20;
    private static final int MAX_TRAVEL_TICKS = 60;
    private static final int TRAVEL_EASE_EDGE_TICKS = MIN_TRAVEL_TICKS / 2;
    private static final double TRAVEL_BLOCKS_PER_TICK = 30.0D;
    private static final int PRE_PUSH_WAIT_TICKS = 10;
    private static final int CROSS_DIMENSION_WAIT_TICKS = 20;
    private static final int ENTER_BODY_TICKS = 12;
    private static final int POST_RELEASE_EFFECT_TICKS = 8;
    private static final int HUD_FADE_TICKS = 8;
    private static final int CUSTOM_TRAVEL_SOUND_FADE_TICKS = 30;

    private static final int FALLBACK_TERRAIN_SWEEP_TICKS = PRE_TRAVEL_WAIT_TICKS;
    private static final int FALLBACK_TERRAIN_FADE_OUT_DELAY_TICKS = 4;
    private static final float FALLBACK_TERRAIN_SWEEP_BAND = 0.22F;
    private static final float SHADER_SCREEN_MASK_SWEEP_BAND = 0.22F;
    private static final int TERRAIN_VISIBILITY_REFRESH_MIN_SECTION_STEP = 4;
    private static final boolean SHADER_SCREEN_MASK_ENABLED = true;
    private static final boolean VOXY_LOADED = FabricLoader.getInstance().isModLoaded("voxy");
    private static final boolean DISTANT_HORIZONS_LOADED = FabricLoader.getInstance().isModLoaded("distanthorizons");
    private static final boolean BOBBY_LOADED = FabricLoader.getInstance().isModLoaded("bobby");
    private static final boolean SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");

    private static final float TRAVEL_EASE_CONTROL_X1 = 0.55F;
    private static final float TRAVEL_EASE_CONTROL_X2 = 0.55F;
    private static final double ZOOM_LOOK_SHAKE_PITCH_AMPLITUDE = 1.05D;
    private static final double ZOOM_LOOK_SHAKE_YAW_AMPLITUDE = 0.15D;
    private static final float BODY_LOOK_SPEED_MULTIPLIER = 1.5F;
    private static final float TOP_DOWN_PITCH = 90.0F;
    private static final float MINECRAFT_STEP_SOUND_VOLUME = 0.86F;
    private static final float MINECRAFT_STEP_SOUND_PITCH = 1.0F;
    private static final float MINECRAFT_TRAVEL_SOUND_VOLUME = 0.48F;
    private static final float MINECRAFT_TRAVEL_SOUND_PITCH = 1.0F;
    private static final float MINECRAFT_BODY_SOUND_VOLUME = 1.0F;
    private static final float MINECRAFT_BODY_SOUND_PITCH = 0.8F;
    private static final int MINECRAFT_BODY_SOUND_LAYERS = 3;

    private static Runnable pendingAction;
    private static Vec3 startFeet;
    private static Vec3 startEye;
    private static Vec3 plannedTargetFeet;
    private static boolean plannedTargetFeetStable = true;
    private static Vec3 actualTargetFeet;
    private static Vec3 arrivalCameraFeet;
    private static Vec3 lastObservedPlayerFeet;
    private static Vec3 enterBodyTargetEye;
    private static String plannedTargetDimensionId;
    private static ResourceKey<Level> startDimension;
    private static CameraType previousCameraType;
    private static boolean previousHudHidden;
    private static boolean previousSmartCull;
    private static float startYaw;
    private static float startPitch;
    private static int ticks;
    private static int travelTicks = DEFAULT_TRAVEL_TICKS;
    private static int totalTicks = getFixedTotalTicks() + DEFAULT_TRAVEL_TICKS;
    private static boolean commandSent;
    private static boolean cameraReleased;
    private static boolean hudSuppressed;
    private static boolean previousHardCutCullingActive;
    private static boolean skipTravel;
    private static int skipTravelArrivalDelayTicks;
    private static float enterBodyTargetYaw;
    private static float enterBodyTargetPitch;
    private static int lastVisibilitySectionX = Integer.MIN_VALUE;
    private static int lastVisibilitySectionY = Integer.MIN_VALUE;
    private static int lastVisibilitySectionZ = Integer.MIN_VALUE;
    private static FadingTravelSound activeTravelSound;

    private TeleportTransitionController() {
    }

    static void start(Minecraft client, String command) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        boolean stablePlannedTarget = !TeleportDestinationParser.usesRelativeCoordinates(command)
                || GtaLikeTeleportConfig.isPlayerFreezeEnabled();
        start(
                client,
                TeleportDestinationParser.parse(command, player),
                TeleportDestinationParser.parseDimension(command),
                () -> GtaLikeTeleportClient.sendDeferredCommand(command),
                true,
                stablePlannedTarget
        );
    }

    static void start(Minecraft client, Vec3 plannedTarget, Runnable action) {
        start(client, plannedTarget, null, action, true);
    }

    static void start(Minecraft client, Vec3 plannedTarget, Runnable action, boolean closeOpenScreen) {
        start(client, plannedTarget, null, action, closeOpenScreen);
    }

    static void start(Minecraft client, Vec3 plannedTarget, String targetDimensionId, Runnable action) {
        start(client, plannedTarget, targetDimensionId, action, true);
    }

    static void start(Minecraft client, Vec3 plannedTarget, String targetDimensionId, Runnable action, boolean closeOpenScreen) {
        start(client, plannedTarget, targetDimensionId, action, closeOpenScreen, true);
    }

    private static void start(Minecraft client, Vec3 plannedTarget, String targetDimensionId, Runnable action, boolean closeOpenScreen, boolean stablePlannedTarget) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        stopCustomTravelSound(client);

        pendingAction = action;
        startFeet = getFeetPos(player);
        startEye = player.getEyePosition();
        plannedTargetFeet = plannedTarget;
        plannedTargetFeetStable = stablePlannedTarget;
        actualTargetFeet = null;
        arrivalCameraFeet = stablePlannedTarget ? plannedTarget : null;
        lastObservedPlayerFeet = startFeet;
        enterBodyTargetEye = null;
        startDimension = client.level == null ? Level.OVERWORLD : client.level.dimension();
        plannedTargetDimensionId = normalizeTargetDimensionId(targetDimensionId);
        previousCameraType = client.options.getCameraType();
        previousHudHidden = isHudHidden(client);
        previousSmartCull = client.smartCull;
        startYaw = player.getYRot();
        startPitch = player.getXRot();
        ticks = 0;
        skipTravel = shouldSkipTravelForTargetDimension(plannedTargetDimensionId);
        skipTravelArrivalDelayTicks = 0;
        enterBodyTargetYaw = 0.0F;
        enterBodyTargetPitch = 0.0F;
        travelTicks = skipTravel ? 0 : calculateTravelTicks(startFeet, plannedTargetFeet);
        totalTicks = getFixedTotalTicks() + travelTicks;
        commandSent = false;
        cameraReleased = false;
        hudSuppressed = false;
        previousHardCutCullingActive = false;
        resetVisibilityRefreshState();

        Screen currentScreen = client.gui == null ? null : client.gui.screen();
        if (currentScreen != null) {
            client.gui.setScreen(null);
        }

        client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        client.smartCull = false;
        refreshLevelRenderer(client);
        SodiumCompat.beginTransition(client, isFallbackTerrainMode());
    }

    static void tick(Minecraft client) {
        if (!isRunning()) {
            return;
        }

        if (client.player == null || client.level == null || client.getConnection() == null) {
            clear(client);
            return;
        }

        if (client.player.isDeadOrDying()) {
            clear(client);
            return;
        }

        ticks++;

        updateHardCutTerrainState(client);
        updateHudVisibility(client);

        if (ticks == 1) {
            playBodyTransitionSound(client, false);
        }
        if (ticks == getEnterStartTick()) {
            playBodyTransitionSound(client, true);
        }

        playStepSound(client, ticks);

        if (!skipTravel && ticks == getTravelStartTick()) {
            playTravelSound(client);
        }
        if (!skipTravel && ticks == getTravelEndTick()) {
            fadeCustomTravelSound();
        }

        if (!commandSent && ticks >= getCommandSendTick()) {
            commandSent = true;
            runPendingAction();
        }

        Vec3 playerFeet = getFeetPos(client.player);
        if (commandSent && ticks > getPullEndTick() + 2) {
            if (actualTargetFeet == null && hasArrivedAtTeleportTarget(client, playerFeet)) {
                recordActualTargetFeet(playerFeet);
                if (skipTravel) {
                    skipTravelArrivalDelayTicks = Math.max(0, ticks - getTravelEndTick());
                    totalTicks += skipTravelArrivalDelayTicks;
                }
            }
        }
        lastObservedPlayerFeet = playerFeet;

        if (!cameraReleased && ticks >= totalTicks) {
            if (!commandSent) {
                commandSent = true;
                runPendingAction();
            }

            cameraReleased = true;
            restoreCameraType(client);
            restoreSmartCull(client);
            SodiumCompat.endTransition();
        }

        if (ticks >= totalTicks + POST_RELEASE_EFFECT_TICKS) {
            clear(client);
        }
    }

    static boolean isRunning() {
        return pendingAction != null;
    }

    static float getProgress() {
        if (!isRunning()) {
            return 0.0F;
        }

        return Mth.clamp(ticks / (float) totalTicks, 0.0F, 1.0F);
    }

    public static int getTicks() {
        return ticks;
    }

    public static boolean shouldBlockPlayerInput() {
        return GtaLikeTeleportConfig.isPlayerFreezeEnabled() && isRunning() && !cameraReleased;
    }

    public static boolean shouldBlockGameplayInput() {
        if (!shouldBlockPlayerInput()) {
            return false;
        }

        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client == null || client.gui == null ? null : client.gui.screen();
        return currentScreen == null;
    }

    public static boolean shouldSuppressGameMenu() {
        return false;
    }

    public static boolean shouldHideLocalPlayerModel() {
        if (!isRunning() || cameraReleased) {
            return false;
        }

        int hideTicks = GtaLikeTeleportConfig.getLocalPlayerHideTicks();
        return hideTicks > 0 && (ticks <= hideTicks || ticks >= totalTicks - hideTicks);
    }

    public static CameraFrame getCameraFrame(float tickProgress) {
        if (!isRunning()) {
            return null;
        }

        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return null;
        }

        if (cameraReleased) {
            return null;
        }

        float frameTick = Math.min(ticks + tickProgress, totalTicks);
        if (frameTick <= EXIT_BODY_TICKS) {
            return exitBodyFrame(frameTick / EXIT_BODY_TICKS);
        }

        if (frameTick <= getPullStartTick()) {
            return startBodyHoldFrame(frameTick);
        }

        int pullMotionEnd = getPullMotionEndTick();
        if (frameTick <= pullMotionEnd) {
            return pullFrame((frameTick - getPullStartTick()) / Math.max(1.0F, pullMotionEnd - getPullStartTick()), frameTick);
        }

        if (frameTick <= getTravelStartTick()) {
            return topDownFrame(startFeet, startYaw, frameTick, getStartTravelAltitude());
        }

        int travelStart = getTravelStartTick();
        if (!skipTravel && frameTick <= travelStart + travelTicks) {
            return travelFrame((frameTick - travelStart) / travelTicks, frameTick);
        }

        if (skipTravel && actualTargetFeet == null) {
            return topDownFrame(startFeet, startYaw, frameTick, getStartTravelAltitude());
        }

        int pushMotionStart = getPushMotionStartTick();
        if (frameTick <= pushMotionStart) {
            return prePushTopDownFrame(getArrivalCameraFeet(), startYaw, frameTick);
        }

        if (frameTick < getEnterHoldStartTick()) {
            return pushFrame(player, (frameTick - pushMotionStart) / Math.max(1.0F, getEnterHoldStartTick() - pushMotionStart), frameTick);
        }

        if (frameTick <= getEnterStartTick()) {
            return enterBodyHoldFrame(frameTick);
        }

        return enterBodyFrame(player, (frameTick - getEnterStartTick()) / ENTER_BODY_TICKS);
    }

    public static void requestTerrainVisibilityUpdate(Vec3 cameraPos) {
        if (!isRunning() || cameraReleased) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.levelRenderer == null) {
            return;
        }

        int sectionX = Mth.floor(cameraPos.x / 16.0D);
        int sectionZ = Mth.floor(cameraPos.z / 16.0D);
        if (lastVisibilitySectionX == Integer.MIN_VALUE || lastVisibilitySectionZ == Integer.MIN_VALUE) {
            lastVisibilitySectionX = sectionX;
            lastVisibilitySectionZ = sectionZ;
            return;
        }

        int sectionStep = getTerrainVisibilityRefreshSectionStep(client);
        if (Math.abs(sectionX - lastVisibilitySectionX) < sectionStep
                && Math.abs(sectionZ - lastVisibilitySectionZ) < sectionStep) {
            return;
        }

        lastVisibilitySectionX = sectionX;
        lastVisibilitySectionZ = sectionZ;
        refreshLevelRenderer(client);
        SodiumCompat.scheduleTerrainUpdate();
    }

    public static boolean shouldForceTerrainFrustumApply() {
        return isRunning() && !cameraReleased;
    }

    public static boolean shouldPreferVoxyOnlyTerrain() {
        if (!isVoxyTerrainAvailable() || isDistantHorizonsTerrainAvailable() || isBobbyTerrainAvailable() || !isRunning() || cameraReleased) {
            return false;
        }

        return ticks >= getPullEndTick() && ticks < getPushStartTick();
    }

    public static boolean shouldPreferDistantHorizonsOnlyTerrain() {
        if (!isDistantHorizonsTerrainAvailable() || !isRunning() || cameraReleased || skipTravel) {
            return false;
        }

        return ticks >= getPullEndTick() && ticks < getPushStartTick();
    }

    public static boolean shouldHideFallbackTerrain() {
        if (!isFallbackTerrainMode() || cameraReleased) {
            return false;
        }

        return ticks >= getFallbackTerrainHideTick() && ticks < getFallbackTerrainShowTick();
    }

    public static boolean shouldApplyFallbackTerrainVisibility() {
        return isFallbackTerrainMode() && !cameraReleased;
    }

    public static boolean shouldHardCutFallbackTerrain() {
        return shouldApplyFallbackTerrainVisibility() && IrisCompat.shouldUseHardTerrainCut();
    }

    public static boolean shouldHideHardCutFallbackTerrain() {
        return shouldHardCutFallbackTerrain() && shouldHideFallbackTerrain();
    }

    public static boolean shouldUseHardCutFallbackTerrainCulling() {
        return shouldHardCutFallbackTerrain()
                && !shouldUseShaderScreenMaskOnly()
                && (isFallbackTerrainFadeOutActive() || shouldHideFallbackTerrain());
    }

    public static boolean shouldRecoverHardCutFallbackTerrain() {
        return shouldHardCutFallbackTerrain() && !shouldUseShaderScreenMaskOnly() && ticks >= getFallbackTerrainShowTick();
    }

    public static boolean shouldUseShaderScreenMaskOnly() {
        return shouldUseScreenMaskFallbackTerrain();
    }

    private static boolean shouldUseScreenMaskFallbackTerrain() {
        return false;
    }
    public static boolean shouldApplyVanillaFallbackTerrainVisibility() {
        return shouldApplyFallbackTerrainVisibility()
                && !shouldUseShaderScreenMaskOnly()
                && ticks >= getVanillaTerrainFadeOutStartTick()
                && ticks < getVanillaTerrainFadeInEndTick();
    }


    public static boolean shouldHideFallbackSkyCelestials() {
        return isFallbackTerrainMode() && !cameraReleased;
    }

    public static float getFallbackTerrainSectionVisibility(BlockPos renderOrigin) {
        return getFallbackTerrainSectionVisibility(
                renderOrigin.getX() + 8.0D,
                renderOrigin.getY() + 8.0D,
                renderOrigin.getZ() + 8.0D
        );
    }

    public static float getFallbackTerrainSectionVisibility(double centerX, double centerY, double centerZ) {
        if (!shouldApplyFallbackTerrainVisibility()) {
            return 1.0F;
        }

        float frameTick = ticks;
        int fadeOutStart = getVanillaTerrainFadeOutStartTick();
        int fadeOutEnd = getVanillaTerrainFadeOutEndTick();
        int fadeInStart = getVanillaTerrainFadeInStartTick();
        int fadeInEnd = getVanillaTerrainFadeInEndTick();

        if (frameTick >= fadeOutEnd && frameTick < fadeInStart) {
            return 0.0F;
        }

        if (frameTick >= fadeOutStart && frameTick < fadeOutEnd) {
            float progress = smoothStep((frameTick - fadeOutStart) / FALLBACK_TERRAIN_SWEEP_TICKS);
            float threshold = 1.0F - getDistanceOrderTowardPoint(centerX, centerZ, startFeet, getBestTargetFeet());
            return 1.0F - smoothStep((progress - threshold) / FALLBACK_TERRAIN_SWEEP_BAND);
        }

        if (frameTick >= fadeInStart && frameTick < fadeInEnd) {
            float progress = smoothStep((frameTick - fadeInStart) / FALLBACK_TERRAIN_SWEEP_TICKS);
            float threshold = getDistanceOrderTowardPoint(centerX, centerZ, getBestTargetFeet(), startFeet);
            return smoothStep((progress - threshold) / FALLBACK_TERRAIN_SWEEP_BAND);
        }

        return 1.0F;
    }

    public static float getShaderScreenMaskSectionOpacity(double centerX, double centerY, double centerZ) {
        if (!shouldUseShaderScreenMaskOnly()) {
            return 0.0F;
        }

        float frameTick = ticks;
        int fadeOutStart = getFallbackTerrainFadeOutStartTick();
        int fadeOutEnd = getFallbackTerrainFadeOutEndTick();
        int fadeInStart = getFallbackTerrainFadeInStartTick();
        int fadeInEnd = getFallbackTerrainFadeInEndTick();

        if (frameTick >= fadeOutEnd && frameTick < fadeInStart) {
            return 1.0F;
        }

        if (frameTick >= fadeOutStart && frameTick < fadeOutEnd) {
            float progress = smoothStep((frameTick - fadeOutStart) / FALLBACK_TERRAIN_SWEEP_TICKS);
            float threshold = getShaderMaskSweepThreshold(getDistanceOrderTowardPoint(centerX, centerZ, startFeet, getBestTargetFeet()));
            return smoothStep((progress - threshold) / SHADER_SCREEN_MASK_SWEEP_BAND);
        }

        if (frameTick >= fadeInStart && frameTick < fadeInEnd) {
            float progress = smoothStep((frameTick - fadeInStart) / FALLBACK_TERRAIN_SWEEP_TICKS);
            float threshold = getShaderMaskSweepThreshold(1.0F - getDistanceOrderTowardPoint(centerX, centerZ, getBestTargetFeet(), startFeet));
            return 1.0F - smoothStep((progress - threshold) / SHADER_SCREEN_MASK_SWEEP_BAND);
        }

        return 0.0F;
    }

    public static boolean shouldRenderFallbackTerrainSection(double centerX, double centerY, double centerZ) {
        return getFallbackTerrainSectionVisibility(centerX, centerY, centerZ) >= 0.5F;
    }

    public static boolean shouldCullFallbackTerrainSection(double centerX, double centerY, double centerZ) {
        if (!shouldUseHardCutFallbackTerrainCulling()) {
            return false;
        }

        if (shouldHideFallbackTerrain()) {
            return true;
        }

        return getFallbackTerrainSectionVisibility(centerX, centerY, centerZ) < 0.5F;
    }

    public static float getStepEffectIntensity(float tickProgress) {
        return getStepEffectIntensity(tickProgress, true);
    }

    public static float getCameraMotionStepEffectIntensity(float tickProgress) {
        return getStepEffectIntensity(tickProgress, false);
    }

    private static float getStepEffectIntensity(float tickProgress, boolean includeReleasePulse) {
        if (!isRunning()) {
            return 0.0F;
        }

        float frameTick = ticks + tickProgress;
        float intensity = 0.0F;
        intensity = Math.max(intensity, pulseAfter(frameTick, getPullStageTick(0)));
        intensity = Math.max(intensity, pulseAfter(frameTick, getPullStageTick(1)));
        intensity = Math.max(intensity, pulseAfter(frameTick, getPullStageTick(2)));

        intensity = Math.max(intensity, pulseAfter(frameTick, getPushStageTick(1)));
        intensity = Math.max(intensity, pulseAfter(frameTick, getPushStageTick(2)));
        if (includeReleasePulse) {
            intensity = Math.max(intensity, pulseAfter(frameTick, getEnterHoldStartTick()));
        }
        return intensity;
    }

    public static float getHudFadeOverlayIntensity(float tickProgress) {
        if (!isRunning()) {
            return 0.0F;
        }

        float frameTick = ticks + tickProgress;
        float releaseTick = totalTicks;

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
    public static float getShaderScreenMaskIntensity(float tickProgress) {
        if (!shouldUseScreenMaskFallbackTerrain() || cameraReleased) {
            return 0.0F;
        }

        float frameTick = ticks + tickProgress;
        int fadeOutStart = getFallbackTerrainFadeOutStartTick();
        int fadeOutEnd = getFallbackTerrainFadeOutEndTick();
        int fadeInStart = getFallbackTerrainFadeInStartTick();
        int fadeInEnd = getFallbackTerrainFadeInEndTick();
        float maxIntensity = 1.0F;

        if (shouldUseShaderScreenMaskOnly()) {
            return frameTick >= fadeOutStart && frameTick < fadeInEnd ? maxIntensity : 0.0F;
        }

        if (frameTick >= fadeOutEnd && frameTick < fadeInStart) {
            return maxIntensity;
        }

        if (frameTick >= fadeOutStart && frameTick < fadeOutEnd) {
            return smoothStep((frameTick - fadeOutStart) / FALLBACK_TERRAIN_SWEEP_TICKS) * maxIntensity;
        }

        if (frameTick >= fadeInStart && frameTick < fadeInEnd) {
            return (1.0F - smoothStep((frameTick - fadeInStart) / FALLBACK_TERRAIN_SWEEP_TICKS)) * maxIntensity;
        }

        return 0.0F;
    }

    private static String normalizeTargetDimensionId(String targetDimensionId) {
        return DimensionIds.normalize(targetDimensionId);
    }

    private static boolean shouldSkipTravelForTargetDimension(String targetDimensionId) {
        return targetDimensionId != null
                && startDimension != null
                && !GtaLikeTeleportConfig.isCrossDimensionTravelEnabled()
                && !targetDimensionId.equals(DimensionIds.fromResourceKey(startDimension));
    }

    private static boolean hasArrivedAtTeleportTarget(Minecraft client, Vec3 playerFeet) {
        if (skipTravel && plannedTargetDimensionId != null && client.level != null) {
            return plannedTargetDimensionId.equals(DimensionIds.fromResourceKey(client.level.dimension()));
        }

        return isNearPlannedTarget(playerFeet) || hasTeleportedSinceLastObservation(playerFeet);
    }

    private static boolean isNearPlannedTarget(Vec3 playerFeet) {
        if (!plannedTargetFeetStable || plannedTargetFeet == null) {
            return false;
        }

        double dx = playerFeet.x - plannedTargetFeet.x;
        double dz = playerFeet.z - plannedTargetFeet.z;
        double dy = Math.abs(playerFeet.y - plannedTargetFeet.y);
        return dx * dx + dz * dz <= 16.0D && dy <= 8.0D;
    }

    private static boolean hasTeleportedSinceLastObservation(Vec3 playerFeet) {
        return lastObservedPlayerFeet != null && playerFeet.distanceToSqr(lastObservedPlayerFeet) > 9.0D;
    }
    private static int calculateTravelTicks(Vec3 fromFeet, Vec3 toFeet) {
        if (fromFeet == null || toFeet == null) {
            return DEFAULT_TRAVEL_TICKS;
        }

        double dx = toFeet.x - fromFeet.x;
        double dz = toFeet.z - fromFeet.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        int scaledTicks = Mth.ceil(horizontalDistance / TRAVEL_BLOCKS_PER_TICK);
        return Mth.clamp(scaledTicks, MIN_TRAVEL_TICKS, MAX_TRAVEL_TICKS);
    }

    private static void recordActualTargetFeet(Vec3 playerFeet) {
        actualTargetFeet = playerFeet;
        if (!skipTravel && !plannedTargetFeetStable && ticks <= getTravelStartTick()) {
            travelTicks = calculateTravelTicks(startFeet, actualTargetFeet);
            totalTicks = getFixedTotalTicks() + travelTicks;
        }
        if (skipTravel || ticks < getTravelEndTick() || arrivalCameraFeet == null) {
            arrivalCameraFeet = playerFeet;
        }
    }
    private static CameraFrame exitBodyFrame(float progress) {
        float heightProgress = smoothStep(progress);
        float lookProgress = bodyLookProgress(progress);
        Vec3 source = startEye == null ? startFeet.add(0.0D, 1.6D, 0.0D) : startEye;
        Vec3 target = startFeet.add(0.0D, getBodyCameraHeight(), 0.0D);
        return new CameraFrame(source.lerp(target, heightProgress), startYaw, lerpFloat(startPitch, TOP_DOWN_PITCH, lookProgress));
    }

    private static CameraFrame startBodyHoldFrame(float frameTick) {
        float progress = (frameTick - EXIT_BODY_TICKS) / getBodyHoldTicks();
        double altitude = getBodyCameraHeight() + glideProgress(progress) * getBodyGlideHeight();
        return bodyHoldFrame(startFeet, startYaw, altitude);
    }

    private static CameraFrame enterBodyHoldFrame(float frameTick) {
        float progress = (frameTick - getEnterHoldStartTick()) / getBodyHoldTicks();
        double altitude = getBodyCameraHeight() + (1.0D - glideProgress(progress)) * getBodyGlideHeight();
        return bodyHoldFrame(getArrivalCameraFeet(), startYaw, altitude);
    }

    private static CameraFrame bodyHoldFrame(Vec3 feet, float yaw, double altitude) {
        return new CameraFrame(feet.add(0.0D, altitude, 0.0D), yaw, TOP_DOWN_PITCH);
    }

    private static CameraFrame pullFrame(float progress, float frameTick) {
        Vec3 pos = startFeet.add(0.0D, pullAltitude(frameTick), 0.0D);
        return applyZoomShake(pos, startYaw, TOP_DOWN_PITCH, frameTick, 1.0F);
    }

    private static CameraFrame topDownFrame(Vec3 feet, float yaw, float frameTick, double altitude) {
        Vec3 pos = feet.add(0.0D, altitude, 0.0D);
        return applyZoomShake(pos, yaw, TOP_DOWN_PITCH, frameTick, 1.0F);
    }

    private static CameraFrame prePushTopDownFrame(Vec3 feet, float yaw, float frameTick) {
        return topDownFrame(feet, yaw, frameTick, getTargetTravelAltitude());
    }

    private static CameraFrame travelFrame(float progress, float frameTick) {
        Vec3 source = startFeet.add(0.0D, getStartTravelAltitude(), 0.0D);
        Vec3 target = getTravelTargetFeet().add(0.0D, getTargetTravelAltitude(), 0.0D);
        Vec3 pos = source.lerp(target, travelEaseProgress(progress));

        return applyZoomShake(pos, startYaw, TOP_DOWN_PITCH, frameTick, 1.0F);
    }

    private static float travelEaseProgress(float progress) {
        double total = Math.max(1.0D, travelTicks);
        double edge = Math.min(TRAVEL_EASE_EDGE_TICKS, total * 0.5D);
        double elapsed = Mth.clamp(progress, 0.0F, 1.0F) * total;
        double weightedTotal = Math.max(1.0D, total - edge);

        if (elapsed < edge) {
            return (float) (edge * integratedSmoothStep(elapsed / edge) / weightedTotal);
        }

        double steadyEnd = total - edge;
        if (elapsed <= steadyEnd) {
            return (float) ((edge * 0.5D + elapsed - edge) / weightedTotal);
        }

        double decelElapsed = elapsed - steadyEnd;
        double beforeDecel = edge * 0.5D + Math.max(0.0D, total - edge * 2.0D);
        return (float) ((beforeDecel + edge * integratedSmoothStepDown(decelElapsed / edge)) / weightedTotal);
    }

    private static double integratedSmoothStep(double value) {
        double x = Mth.clamp(value, 0.0D, 1.0D);
        return x * x * x - 0.5D * x * x * x * x;
    }

    private static double integratedSmoothStepDown(double value) {
        double x = Mth.clamp(value, 0.0D, 1.0D);
        return x - integratedSmoothStep(x);
    }

    private static CameraFrame pushFrame(LocalPlayer player, float progress, float frameTick) {
        Vec3 pos = getArrivalCameraFeet().add(0.0D, pushAltitude(frameTick), 0.0D);
        return applyZoomShake(pos, startYaw, TOP_DOWN_PITCH, frameTick, 1.0F);
    }

    private static CameraFrame enterBodyFrame(LocalPlayer player, float progress) {
        captureEnterBodyTarget(player);
        float heightProgress = smoothStep(progress);
        float lookProgress = bodyLookProgress(progress);
        Vec3 source = getArrivalCameraFeet().add(0.0D, getBodyCameraHeight(), 0.0D);
        Vec3 target = enterBodyTargetEye == null ? player.getEyePosition() : enterBodyTargetEye;
        float yaw = lerpDegrees(startYaw, enterBodyTargetYaw, lookProgress);
        float pitch = lerpFloat(TOP_DOWN_PITCH, enterBodyTargetPitch, lookProgress);
        return new CameraFrame(source.lerp(target, heightProgress), yaw, pitch);
    }

    private static void captureEnterBodyTarget(LocalPlayer player) {
        if (enterBodyTargetEye != null) {
            return;
        }

        enterBodyTargetEye = player.getEyePosition();
        enterBodyTargetYaw = player.getYRot();
        enterBodyTargetPitch = player.getXRot();
    }

    private static Vec3 getBestTargetFeet() {
        if (actualTargetFeet != null) {
            return actualTargetFeet;
        }

        if (plannedTargetFeetStable && plannedTargetFeet != null) {
            return plannedTargetFeet;
        }

        return startFeet;
    }

    private static Vec3 getArrivalCameraFeet() {
        if (arrivalCameraFeet != null) {
            return arrivalCameraFeet;
        }

        return getBestTargetFeet();
    }

    private static Vec3 getTravelTargetFeet() {
        return getArrivalCameraFeet();
    }

    private static CameraFrame applyZoomShake(Vec3 pos, float yaw, float pitch, float frameTick, float envelope) {
        double easedEnvelope = Mth.clamp(envelope, 0.0F, 1.0F);
        double verticalWave = (
                Math.sin(frameTick * 0.105D) * 0.74D
                        + Math.sin(frameTick * 0.052D + 1.6D) * 0.26D
        );
        double horizontalWave = (
                Math.sin(frameTick * 0.105D + 2.1D) * 0.78D
                        + Math.sin(frameTick * 0.15D + 0.35D) * 0.22D
        );
        double pitchShake = (Mth.clamp(verticalWave, -1.0D, 1.0D) - 1.0D) * 0.5D * ZOOM_LOOK_SHAKE_PITCH_AMPLITUDE * easedEnvelope;
        double yawShake = horizontalWave * ZOOM_LOOK_SHAKE_YAW_AMPLITUDE * easedEnvelope;

        float shakenYaw = yaw + (float) yawShake;
        float shakenPitch = Mth.clamp(pitch + (float) pitchShake, -90.0F, 90.0F);
        return new CameraFrame(pos, shakenYaw, shakenPitch);
    }

    private static double getStartTravelAltitude() {
        return getZoomOutStageAltitude(2) + getZoomStageGlideHeight();
    }

    private static double getTargetTravelAltitude() {
        return getZoomInStageAltitude(2) + getZoomStageGlideHeight();
    }

    private static double getZoomOutStageAltitude(int index) {
        return GtaLikeTeleportConfig.getZoomOutStageHeights(getStartZoomDimension())[index];
    }

    private static double getZoomInStageAltitude(int index) {
        return GtaLikeTeleportConfig.getZoomInStageHeights(getCurrentZoomDimension())[index];
    }

    private static GtaLikeTeleportConfig.ZoomDimension getStartZoomDimension() {
        return GtaLikeTeleportConfig.ZoomDimension.fromLevel(startDimension);
    }

    private static GtaLikeTeleportConfig.ZoomDimension getCurrentZoomDimension() {
        Minecraft client = Minecraft.getInstance();
        ResourceKey<Level> dimension = client.level == null ? startDimension : client.level.dimension();
        return GtaLikeTeleportConfig.ZoomDimension.fromLevel(dimension);
    }

    private static double pullAltitude(float frameTick) {
        int firstStage = getPullStageTick(0);
        int secondStage = getPullStageTick(1);
        int thirdStage = getPullStageTick(2);
        int pullEnd = getPullMotionEndTick();

        if (frameTick < secondStage) {
            return departingAltitude(getZoomOutStageAltitude(0), frameTick, firstStage, secondStage);
        }
        if (frameTick < thirdStage) {
            return departingAltitude(getZoomOutStageAltitude(1), frameTick, secondStage, thirdStage);
        }

        return departingFinalAltitude(getZoomOutStageAltitude(2), frameTick, thirdStage, pullEnd);
    }

    private static double pushAltitude(float frameTick) {
        int pushMotionStart = getPushMotionStartTick();
        int firstStage = getPushStageTick(1);
        int secondStage = getPushStageTick(2);
        int pushEnd = getEnterHoldStartTick();

        if (frameTick < firstStage) {
            return arrivingAltitude(getZoomInStageAltitude(2), frameTick, pushMotionStart, pushMotionStart + getZoomInStageTickLength(0));
        }
        if (frameTick < secondStage) {
            return arrivingAltitude(getZoomInStageAltitude(1), frameTick, firstStage, secondStage);
        }

        return arrivingAltitude(getZoomInStageAltitude(0), frameTick, secondStage, pushEnd);
    }

    private static double departingAltitude(double altitude, float frameTick, int startTick, int endTick) {
        return altitude + zoomStageGlideProgressBetween(frameTick, startTick, endTick) * getZoomStageGlideHeight();
    }

    private static double arrivingAltitude(double altitude, float frameTick, int startTick, int endTick) {
        return altitude + (1.0D - zoomStageGlideProgressBetween(frameTick, startTick, endTick)) * getZoomStageGlideHeight();
    }

    private static double departingFinalAltitude(double altitude, float frameTick, int startTick, int endTick) {
        return altitude + finalZoomOutGlideProgressBetween(frameTick, startTick, endTick) * getZoomStageGlideHeight();
    }

    private static double zoomStageGlideProgressBetween(float frameTick, int startTick, int endTick) {
        float progress = (frameTick - startTick) / Math.max(1.0F, endTick - startTick);
        return linearThenEaseOut(progress);
    }

    private static double finalZoomOutGlideProgressBetween(float frameTick, int startTick, int endTick) {
        float progress = (frameTick - startTick) / Math.max(1.0F, endTick - startTick);
        return longEaseOut(progress);
    }

    private static double linearThenEaseOut(float value) {
        double x = Mth.clamp(value, 0.0F, 1.0F);
        return hermiteEaseOutFromLinear(x, 0.64D, 1.08D);
    }

    private static double longEaseOut(float value) {
        double x = Mth.clamp(value, 0.0F, 1.0F);
        return hermiteEaseOutFromLinear(x, 0.36D, 1.0D);
    }

    private static double hermiteEaseOutFromLinear(double x, double decelStart, double linearSpeed) {
        if (x <= decelStart) {
            return x * linearSpeed;
        }

        double t = (x - decelStart) / (1.0D - decelStart);
        double t2 = t * t;
        double t3 = t2 * t;
        double y0 = decelStart * linearSpeed;
        double m0 = linearSpeed * (1.0D - decelStart);
        double h00 = 2.0D * t3 - 3.0D * t2 + 1.0D;
        double h10 = t3 - 2.0D * t2 + t;
        double h01 = -2.0D * t3 + 3.0D * t2;
        return Mth.clamp(h00 * y0 + h10 * m0 + h01, 0.0D, 1.0D);
    }

    private static double glideProgress(float value) {
        return Mth.clamp(value, 0.0F, 1.0F);
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
        float x = Mth.clamp(value, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    private static float lerpFloat(float from, float to, float progress) {
        float x = Mth.clamp(progress, 0.0F, 1.0F);
        return from + (to - from) * x;
    }

    private static float bodyLookProgress(float progress) {
        return easeOutCubic(Mth.clamp(progress * BODY_LOOK_SPEED_MULTIPLIER, 0.0F, 1.0F));
    }

    private static float easeOutCubic(float progress) {
        float x = Mth.clamp(progress, 0.0F, 1.0F);
        float inverse = 1.0F - x;
        return 1.0F - inverse * inverse * inverse;
    }

    private static float lerpDegrees(float from, float to, float progress) {
        return from + wrapDegrees(to - from) * Mth.clamp(progress, 0.0F, 1.0F);
    }

    private static float wrapDegrees(float value) {
        float wrapped = value % 360.0F;
        if (wrapped >= 180.0F) {
            wrapped -= 360.0F;
        }
        if (wrapped < -180.0F) {
            wrapped += 360.0F;
        }
        return wrapped;
    }

    private static boolean isCameraStepSoundTick(int tick) {
        int firstPullStep = getPullStageTick(0);
        int secondPullStep = getPullStageTick(1);
        int thirdPullStep = getPullStageTick(2);
        int firstPushStep = getPushStageTick(1);
        int secondPushStep = getPushStageTick(2);
        int thirdPushStep = getEnterHoldStartTick();

        return tick == firstPullStep
                || tick == secondPullStep
                || tick == thirdPullStep
                || tick == firstPushStep
                || tick == secondPushStep
                || tick == thirdPushStep;
    }

    private static int getPullStageTick(int stageIndex) {
        int[] stageTicks = GtaLikeTeleportConfig.getZoomOutStageTicks();
        int offset = 0;
        for (int i = 0; i < stageIndex && i < stageTicks.length; i++) {
            offset += stageTicks[i];
        }
        return getPullStartTick() + offset;
    }

    private static int getPushStageTick(int stageIndex) {
        int[] stageTicks = GtaLikeTeleportConfig.getZoomInStageTicks();
        int offset = 0;
        for (int i = 0; i < stageIndex && i < stageTicks.length; i++) {
            offset += stageTicks[i];
        }
        return getPushStartTick() + offset;
    }

    private static int getZoomInStageTickLength(int stageIndex) {
        int[] stageTicks = GtaLikeTeleportConfig.getZoomInStageTicks();
        if (stageIndex >= 0 && stageIndex < stageTicks.length) {
            return Math.max(1, stageTicks[stageIndex]);
        }
        return 1;
    }

    private static void playStepSound(Minecraft client, int tick) {
        if (!isCameraStepSoundTick(tick)) {
            return;
        }

        if (GtaLikeTeleportConfig.isCustomSoundsEnabled()) {
            playUiSound(client, getCustomStepSound(tick), (float) GtaLikeTeleportConfig.getCustomSoundVolume(), 1.0F);
            return;
        }

        playUiSound(client, SoundEvents.RESPAWN_ANCHOR_CHARGE, minecraftSoundVolume(MINECRAFT_STEP_SOUND_VOLUME), MINECRAFT_STEP_SOUND_PITCH);
    }

    private static SoundEvent getCustomStepSound(int tick) {
        if (tick == getPullStageTick(2)) {
            return TeleportSounds.ZOOM_OUT_LONG;
        }
        if (tick == getPushStageTick(1) || tick == getPushStageTick(2)) {
            return TeleportSounds.ZOOM_IN_SHORT;
        }
        if (tick == getEnterHoldStartTick()) {
            return TeleportSounds.ZOOM_IN_LONG;
        }
        return TeleportSounds.ZOOM_OUT_SHORT;
    }

    private static void playTravelSound(Minecraft client) {
        if (GtaLikeTeleportConfig.isCustomSoundsEnabled()) {
            activeTravelSound = new FadingTravelSound((float) GtaLikeTeleportConfig.getCustomSoundVolume());
            client.getSoundManager().play(activeTravelSound);
            return;
        }

        playUiSound(client, SoundEvents.PORTAL_TRIGGER, minecraftSoundVolume(MINECRAFT_TRAVEL_SOUND_VOLUME), MINECRAFT_TRAVEL_SOUND_PITCH);
    }

    private static void playBodyTransitionSound(Minecraft client, boolean enteringPlayer) {
        if (GtaLikeTeleportConfig.isCustomSoundsEnabled()) {
            playUiSound(client, enteringPlayer ? TeleportSounds.CAMERA_IN : TeleportSounds.CAMERA_OUT, (float) GtaLikeTeleportConfig.getCustomSoundVolume(), 1.0F);
            return;
        }

        for (int i = 0; i < MINECRAFT_BODY_SOUND_LAYERS; i++) {
            playUiSound(client, SoundEvents.UI_TOAST_OUT, minecraftSoundVolume(MINECRAFT_BODY_SOUND_VOLUME), MINECRAFT_BODY_SOUND_PITCH);
        }
    }

    private static float minecraftSoundVolume(float baseVolume) {
        return baseVolume * (float) GtaLikeTeleportConfig.getMinecraftSoundVolume();
    }

    private static void playUiSound(Minecraft client, SoundEvent sound, float volume, float pitch) {
        client.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private static void fadeCustomTravelSound() {
        if (activeTravelSound != null) {
            activeTravelSound.fadeOut(CUSTOM_TRAVEL_SOUND_FADE_TICKS);
        }
    }

    private static void stopCustomTravelSound(Minecraft client) {
        if (activeTravelSound != null) {
            client.getSoundManager().stop(activeTravelSound);
            activeTravelSound = null;
        }
    }

    private static float cubicBezierEase(float progress) {
        float x = Mth.clamp(progress, 0.0F, 1.0F);
        float t = x;

        for (int i = 0; i < 5; i++) {
            float currentX = cubicBezier(t, TRAVEL_EASE_CONTROL_X1, TRAVEL_EASE_CONTROL_X2);
            float derivative = cubicBezierDerivative(t, TRAVEL_EASE_CONTROL_X1, TRAVEL_EASE_CONTROL_X2);
            if (Math.abs(derivative) < 0.0001F) {
                break;
            }

            t = Mth.clamp(t - (currentX - x) / derivative, 0.0F, 1.0F);
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

    private static Vec3 getFeetPos(LocalPlayer player) {
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    private static int getCommandSendTick() {
        if (skipTravel) {
            return getTravelStartTick();
        }

        if (plannedTargetFeetStable && plannedTargetFeet != null) {
            return getTravelStartTick() + travelTicks / 2;
        }

        return getPullEndTick();
    }

    private static boolean isFallbackTerrainMode() {
        return isRunning() && !skipTravel && GtaLikeTeleportConfig.isFallbackChunkFadeEnabled() && !isDistantTerrainAvailable();
    }

    private static boolean isDistantTerrainAvailable() {
        return isVoxyTerrainAvailable() || isDistantHorizonsTerrainAvailable() || isBobbyTerrainAvailable();
    }

    private static boolean isVoxyTerrainAvailable() {
        return VOXY_LOADED && VoxyCompat.isRenderingEnabled();
    }

    private static boolean isDistantHorizonsTerrainAvailable() {
        return DISTANT_HORIZONS_LOADED && DistantHorizonsCompat.isRenderingEnabled();
    }

    private static boolean isBobbyTerrainAvailable() {
        return BOBBY_LOADED && BobbyCompat.isRenderingEnabled();
    }

    private static int getFallbackTerrainHideTick() {
        return getFallbackTerrainFadeOutEndTick();
    }

    private static int getFallbackTerrainShowTick() {
        return getFallbackTerrainFadeInStartTick();
    }

    private static boolean isFallbackTerrainFadeOutActive() {
        return ticks >= getFallbackTerrainFadeOutStartTick() && ticks < getFallbackTerrainFadeOutEndTick();
    }

    private static int getBodyHoldTicks() {
        return GtaLikeTeleportConfig.getBodyGlideTicks();
    }

    private static int getPullTicks() {
        int total = 0;
        for (int stageTick : GtaLikeTeleportConfig.getZoomOutStageTicks()) {
            total += stageTick;
        }
        return Math.max(1, total);
    }

    private static int getPushTicks() {
        int total = 0;
        for (int stageTick : GtaLikeTeleportConfig.getZoomInStageTicks()) {
            total += stageTick;
        }
        return Math.max(1, total);
    }

    private static int getFixedTotalTicks() {
        return EXIT_BODY_TICKS + getBodyHoldTicks() + getPullTicks() + getPreTravelWaitTicks() + getPrePushWaitTicks() + getPushTicks() + getBodyHoldTicks() + ENTER_BODY_TICKS;
    }

    private static double getBodyCameraHeight() {
        return GtaLikeTeleportConfig.getBodyCameraHeight();
    }

    private static double getBodyGlideHeight() {
        return GtaLikeTeleportConfig.getBodyGlideHeight();
    }

    private static double getZoomStageGlideHeight() {
        return GtaLikeTeleportConfig.getZoomStageGlideHeight();
    }

    private static int getPullStartTick() {
        return EXIT_BODY_TICKS + getBodyHoldTicks();
    }

    private static int getPullEndTick() {
        return getPullStartTick() + getPullTicks();
    }

    private static int getPreTravelWaitTicks() {
        return skipTravel ? CROSS_DIMENSION_WAIT_TICKS : PRE_TRAVEL_WAIT_TICKS;
    }

    private static int getPrePushWaitTicks() {
        return skipTravel ? CROSS_DIMENSION_WAIT_TICKS : PRE_PUSH_WAIT_TICKS;
    }

    private static int getTravelStartTick() {
        return getPullEndTick() + getPreTravelWaitTicks();
    }

    private static int getTravelEndTick() {
        return getTravelStartTick() + travelTicks;
    }

    private static int getPushStartTick() {
        return getTravelEndTick() + getPrePushWaitTicks() + (skipTravel ? skipTravelArrivalDelayTicks : 0);
    }

    private static int getPullMotionEndTick() {
        return skipTravel ? getPullEndTick() : getTravelStartTick();
    }

    private static int getPushMotionStartTick() {
        return skipTravel ? getPushStartTick() : getTravelEndTick();
    }

    private static int getEnterHoldStartTick() {
        return getPushStartTick() + getPushTicks();
    }

    private static int getEnterStartTick() {
        return getEnterHoldStartTick() + getBodyHoldTicks();
    }

    private static int getFallbackTerrainFadeOutStartTick() {
        return getTravelStartTick() + FALLBACK_TERRAIN_FADE_OUT_DELAY_TICKS;
    }

    private static int getFallbackTerrainFadeOutEndTick() {
        return getFallbackTerrainFadeOutStartTick() + FALLBACK_TERRAIN_SWEEP_TICKS;
    }

    private static int getFallbackTerrainFadeInStartTick() {
        return getTravelEndTick() - FALLBACK_TERRAIN_SWEEP_TICKS;
    }

    private static int getFallbackTerrainFadeInEndTick() {
        return getTravelEndTick();
    }

    private static int getVanillaTerrainFadeOutStartTick() {
        return getPullEndTick();
    }

    private static int getVanillaTerrainFadeOutEndTick() {
        return getPullEndTick() + PRE_TRAVEL_WAIT_TICKS;
    }

    private static int getVanillaTerrainFadeInStartTick() {
        return getTravelEndTick();
    }

    private static int getVanillaTerrainFadeInEndTick() {
        return getVanillaTerrainFadeInStartTick() + FALLBACK_TERRAIN_SWEEP_TICKS;
    }

    private static float getDistanceOrderTowardPoint(double sectionCenterX, double sectionCenterZ, Vec3 centerFeet, Vec3 directionFeet) {
        double radius = getFallbackTerrainRenderRadius();
        double centerDistance = horizontalDistance(centerFeet, directionFeet);
        double distance = horizontalDistance(sectionCenterX, sectionCenterZ, directionFeet);
        double min = Math.max(0.0D, centerDistance - radius);
        double max = centerDistance + radius;
        return (float) Mth.clamp((distance - min) / Math.max(1.0D, max - min), 0.0D, 1.0D);
    }

    private static float getShaderMaskSweepThreshold(float order) {
        return Mth.clamp(order, 0.0F, 1.0F) * (1.0F - SHADER_SCREEN_MASK_SWEEP_BAND);
    }

    private static double horizontalDistance(Vec3 from, Vec3 to) {
        if (from == null || to == null) {
            return 0.0D;
        }

        double dx = from.x - to.x;
        double dz = from.z - to.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static double horizontalDistance(double x, double z, Vec3 to) {
        if (to == null) {
            return 0.0D;
        }

        double dx = x - to.x;
        double dz = z - to.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static double getFallbackTerrainRenderRadius() {
        Minecraft client = Minecraft.getInstance();
        int renderDistance = client.options == null ? 12 : client.options.getEffectiveRenderDistance();
        return Math.max(32.0D, renderDistance * 16.0D);
    }

    private static int getTerrainVisibilityRefreshSectionStep(Minecraft client) {
        int renderDistance = client.options == null ? 12 : client.options.getEffectiveRenderDistance();
        return Math.max(TERRAIN_VISIBILITY_REFRESH_MIN_SECTION_STEP, renderDistance / 2);
    }

    private static Field hudHiddenField;
    private static boolean hudHiddenFieldUnavailable;

    private static boolean isHudHidden(Minecraft client) {
        return client.gui != null && client.gui.hud != null && client.gui.hud.isHidden();
    }

    private static void setHudHidden(Minecraft client, boolean hidden) {
        if (client.gui == null || client.gui.hud == null || hudHiddenFieldUnavailable) {
            return;
        }

        try {
            getHudHiddenField().setBoolean(client.gui.hud, hidden);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            hudHiddenFieldUnavailable = true;
        }
    }

    private static Field getHudHiddenField() throws NoSuchFieldException {
        if (hudHiddenField == null) {
            hudHiddenField = Hud.class.getDeclaredField("isHidden");
            hudHiddenField.setAccessible(true);
        }
        return hudHiddenField;
    }

    private static void refreshLevelRenderer(Minecraft client) {
        if (client.levelRenderer != null) {
            if (client.levelRenderer.viewArea() != null) {
                client.levelRenderer.sectionOcclusionGraph().invalidate();
            }
        }
    }
    private static void restoreCameraType(Minecraft client) {
        if (previousCameraType != null) {
            client.options.setCameraType(previousCameraType);
        }
    }

    private static void restoreSmartCull(Minecraft client) {
        if (client.smartCull != previousSmartCull) {
            client.smartCull = previousSmartCull;
            refreshLevelRenderer(client);
        }
    }

    private static void runPendingAction() {
        if (pendingAction != null) {
            pendingAction.run();
        }
    }

    private static void updateHudVisibility(Minecraft client) {
        if (!hudSuppressed && ticks >= HUD_FADE_TICKS) {
            setHudHidden(client, true);
            hudSuppressed = true;
        }

        if (hudSuppressed && cameraReleased) {
            setHudHidden(client, previousHudHidden);
            hudSuppressed = false;
        }
    }

    private static void updateHardCutTerrainState(Minecraft client) {
        boolean active = shouldUseHardCutFallbackTerrainCulling();
        if (active == previousHardCutCullingActive) {
            return;
        }

        previousHardCutCullingActive = active;
        resetVisibilityRefreshState();
        if (client.levelRenderer != null) {
            refreshLevelRenderer(client);
        }
        SodiumCompat.scheduleTerrainUpdate();
    }

    private static void clear(Minecraft client) {
        restoreCameraType(client);
        restoreSmartCull(client);
        SodiumCompat.endTransition();
        stopCustomTravelSound(client);
        setHudHidden(client, previousHudHidden);

        pendingAction = null;
        startFeet = null;
        startEye = null;
        plannedTargetFeet = null;
        plannedTargetFeetStable = true;
        actualTargetFeet = null;
        arrivalCameraFeet = null;
        lastObservedPlayerFeet = null;
        enterBodyTargetEye = null;
        plannedTargetDimensionId = null;
        startDimension = null;
        previousCameraType = null;
        previousHudHidden = false;
        previousSmartCull = false;
        startYaw = 0.0F;
        startPitch = 0.0F;
        enterBodyTargetYaw = 0.0F;
        enterBodyTargetPitch = 0.0F;
        ticks = 0;
        travelTicks = DEFAULT_TRAVEL_TICKS;
        totalTicks = getFixedTotalTicks() + DEFAULT_TRAVEL_TICKS;
        commandSent = false;
        cameraReleased = false;
        hudSuppressed = false;
        previousHardCutCullingActive = false;
        skipTravel = false;
        resetVisibilityRefreshState();
    }

    private static void resetVisibilityRefreshState() {
        lastVisibilitySectionX = Integer.MIN_VALUE;
        lastVisibilitySectionY = Integer.MIN_VALUE;
        lastVisibilitySectionZ = Integer.MIN_VALUE;
    }

    private static final class FadingTravelSound extends AbstractTickableSoundInstance {
        private final float baseVolume;
        private int fadeTicks;
        private int fadeTicksRemaining;

        private FadingTravelSound(float volume) {
            super(TeleportSounds.TELEPORT, SoundSource.UI, SoundInstance.createUnseededRandom());
            this.baseVolume = volume;
            this.volume = volume;
            this.pitch = 1.0F;
            this.relative = true;
            this.attenuation = SoundInstance.Attenuation.NONE;
        }

        private void fadeOut(int ticks) {
            this.fadeTicks = Math.max(1, ticks);
            this.fadeTicksRemaining = this.fadeTicks;
        }

        @Override
        public void tick() {
            if (this.fadeTicksRemaining <= 0) {
                return;
            }

            this.volume = this.baseVolume * (this.fadeTicksRemaining / (float) this.fadeTicks);
            this.fadeTicksRemaining--;
            if (this.fadeTicksRemaining <= 0) {
                this.volume = 0.0F;
                stop();
                if (activeTravelSound == this) {
                    activeTravelSound = null;
                }
            }
        }
    }

    public record CameraFrame(Vec3 pos, float yaw, float pitch) {
    }
}






