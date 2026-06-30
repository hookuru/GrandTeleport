package dev.codex.gtaliketeleport.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.textures.GpuSampler;
import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.caffeinemc.mods.sodium.client.gpu.device.batch.MultiDrawBatch;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.LocalSectionIndex;
import net.caffeinemc.mods.sodium.client.render.chunk.UniformBufferManager;
import net.caffeinemc.mods.sodium.client.render.chunk.data.SectionRenderDataStorage;
import net.caffeinemc.mods.sodium.client.render.chunk.data.SectionRenderDataUnsafe;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderListIterable;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import net.caffeinemc.mods.sodium.client.util.iterator.ByteIterator;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer", remap = false)
public abstract class SodiumDefaultChunkRendererMixin {
    @Unique
    private static final int MIN_CHUNK_SECTION_FADE_MILLIS = 150;
    @Unique
    private static RenderRegion gtalikeTeleport$currentCommandRegion;
    @Unique
    private static boolean gtalikeTeleport$skipCurrentCommandSection;
    @Unique
    private static final Map<TerrainRenderPass, Boolean> gtalikeTeleport$lastHardCutCullingStates = new IdentityHashMap<>();
    @Unique
    private static final Map<TerrainRenderPass, Integer> gtalikeTeleport$lastCullingInvalidationTicks = new IdentityHashMap<>();
    @Unique
    private static Field gtalikeTeleport$uniformBufferManagerField;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void gtalikeTeleport$writeFallbackTerrainFade(
            ChunkRenderMatrices matrices,
            ChunkRenderListIterable renderLists,
            TerrainRenderPass renderPass,
            CameraTransform camera,
            FogParameters fog,
            boolean useTranslucentSort,
            GpuSampler sampler,
            GpuBuffer uniformBuffer,
            GpuBuffer sectionTimeInfo,
            CallbackInfo ci
    ) {
        if (TeleportTransitionController.shouldPreferDistantHorizonsOnlyTerrain()) {
            gtalikeTeleport$lastHardCutCullingStates.clear();
            gtalikeTeleport$lastCullingInvalidationTicks.clear();
            ci.cancel();
            return;
        }

        if (!TeleportTransitionController.shouldApplyVanillaFallbackTerrainVisibility()) {
            gtalikeTeleport$lastHardCutCullingStates.clear();
            gtalikeTeleport$lastCullingInvalidationTicks.clear();
            return;
        }

        UniformBufferManager uniformBufferManager = getUniformBufferManager();
        if (uniformBufferManager == null) {
            return;
        }

        int fadeDurationMillis = getChunkSectionFadeMillis();
        boolean hardCutCulling = TeleportTransitionController.shouldUseHardCutFallbackTerrainCulling();
        boolean recoverHardCutBatches = TeleportTransitionController.shouldRecoverHardCutFallbackTerrain();
        boolean invalidateHardCutBatches = shouldInvalidateCachedBatchesForHardCut(renderPass, hardCutCulling);
        Iterator<ChunkRenderList> iterator = renderLists.iterator(renderPass.isTranslucent());
        while (iterator.hasNext()) {
            ChunkRenderList renderList = iterator.next();
            RenderRegion region = renderList.getRegion();
            if (region == null || region.getResources() == null) {
                continue;
            }

            writeSectionFadeTimes(renderList, region, uniformBufferManager, renderPass, fadeDurationMillis);
            if (invalidateHardCutBatches) {
                invalidateCachedBatchForHardCut(region, renderPass, hardCutCulling);
            } else if (recoverHardCutBatches) {
                recoverEmptyCachedBatchForHardCut(region, renderPass);
            }
        }
    }

    @Inject(method = "fillCommandBuffer", at = @At("HEAD"), require = 0)
    private static void gtalikeTeleport$beginFallbackTerrainCommandBuffer(
            MultiDrawBatch batch,
            RenderRegion region,
            SectionRenderDataStorage storage,
            ChunkRenderList renderList,
            CameraTransform camera,
            TerrainRenderPass renderPass,
            boolean useBlockFaceCulling,
            boolean useIndexedTessellation,
            CallbackInfo ci
    ) {
        gtalikeTeleport$currentCommandRegion = region;
        gtalikeTeleport$skipCurrentCommandSection = false;
    }

    @Inject(method = "fillCommandBuffer", at = @At("RETURN"), require = 0)
    private static void gtalikeTeleport$endFallbackTerrainCommandBuffer(
            MultiDrawBatch batch,
            RenderRegion region,
            SectionRenderDataStorage storage,
            ChunkRenderList renderList,
            CameraTransform camera,
            TerrainRenderPass renderPass,
            boolean useBlockFaceCulling,
            boolean useIndexedTessellation,
            CallbackInfo ci
    ) {
        gtalikeTeleport$currentCommandRegion = null;
        gtalikeTeleport$skipCurrentCommandSection = false;
    }

    @Redirect(
            method = "fillCommandBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/data/SectionRenderDataStorage;getDataPointer(I)J"
            ),
            require = 0
    )
    private static long gtalikeTeleport$captureFallbackTerrainCommandSection(
            SectionRenderDataStorage storage,
            int sectionId
    ) {
        gtalikeTeleport$skipCurrentCommandSection = shouldHardCutSection(gtalikeTeleport$currentCommandRegion, sectionId);
        return storage.getDataPointer(sectionId);
    }

    @Redirect(
            method = "fillCommandBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/data/SectionRenderDataUnsafe;getSliceMask(J)I"
            ),
            require = 0
    )
    private static int gtalikeTeleport$hideFallbackTerrainCommandSection(long dataPointer) {
        if (gtalikeTeleport$skipCurrentCommandSection) {
            return 0;
        }

        return SectionRenderDataUnsafe.getSliceMask(dataPointer);
    }

    private static void writeSectionFadeTimes(
            ChunkRenderList renderList,
            RenderRegion region,
            UniformBufferManager uniformBufferManager,
            TerrainRenderPass renderPass,
            int fadeDurationMillis
    ) {
        int regionId = region.getId();
        if (regionId < 0) {
            return;
        }

        ByteIterator sectionIterator = renderList.sectionsWithGeometryIterator(renderPass.isTranslucent());
        if (sectionIterator == null) {
            return;
        }

        int relativeNow = getRelativeRegionTime(region);
        while (sectionIterator.hasNext()) {
            int sectionId = sectionIterator.nextByteAsInt();
            float visibility = getSectionVisibility(region, sectionId);
            int fadeTime = getFadeTime(relativeNow, fadeDurationMillis, visibility);
            try {
                uniformBufferManager.writeMeshTimes(regionId, sectionId, fadeTime);
            } catch (IllegalStateException ignored) {
                return;
            }
        }
    }

    private static float getSectionVisibility(RenderRegion region, int sectionId) {
        int centerX = (region.getChunkX() + LocalSectionIndex.unpackX(sectionId)) * 16 + 8;
        int centerY = (region.getChunkY() + LocalSectionIndex.unpackY(sectionId)) * 16 + 8;
        int centerZ = (region.getChunkZ() + LocalSectionIndex.unpackZ(sectionId)) * 16 + 8;
        return TeleportTransitionController.getFallbackTerrainSectionVisibility(centerX, centerY, centerZ);
    }

    private static int getFadeTime(int relativeNow, int fadeDurationMillis, float visibility) {
        if (visibility >= 0.999F) {
            return relativeNow - fadeDurationMillis;
        }

        if (visibility <= 0.001F) {
            return relativeNow;
        }

        return relativeNow - Math.round(fadeDurationMillis * visibility);
    }

    private static int getRelativeRegionTime(RenderRegion region) {
        long relativeNow = System.currentTimeMillis() - region.getCreationTime();
        if (relativeNow <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        if (relativeNow >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) relativeNow;
    }

    private static boolean shouldInvalidateCachedBatchesForHardCut(TerrainRenderPass renderPass, boolean hardCutCulling) {
        if (!TeleportTransitionController.shouldHardCutFallbackTerrain()) {
            gtalikeTeleport$lastHardCutCullingStates.clear();
            gtalikeTeleport$lastCullingInvalidationTicks.clear();
            return false;
        }

        Boolean previousHardCutCulling = gtalikeTeleport$lastHardCutCullingStates.get(renderPass);
        if (previousHardCutCulling == null) {
            gtalikeTeleport$lastHardCutCullingStates.put(renderPass, hardCutCulling);
            gtalikeTeleport$lastCullingInvalidationTicks.remove(renderPass);
            return hardCutCulling;
        }

        if (previousHardCutCulling != hardCutCulling) {
            gtalikeTeleport$lastHardCutCullingStates.put(renderPass, hardCutCulling);
            gtalikeTeleport$lastCullingInvalidationTicks.remove(renderPass);
            return true;
        }

        if (!hardCutCulling) {
            return false;
        }

        int tick = TeleportTransitionController.getTicks();
        Integer previousTick = gtalikeTeleport$lastCullingInvalidationTicks.get(renderPass);
        if (previousTick != null && previousTick == tick) {
            return false;
        }

        gtalikeTeleport$lastCullingInvalidationTicks.put(renderPass, tick);
        return true;
    }

    private static void invalidateCachedBatchForHardCut(RenderRegion region, TerrainRenderPass renderPass, boolean hardCutCulling) {
        if (region == null) {
            return;
        }

        MultiDrawBatch batch = region.getCachedBatch(renderPass);
        if (batch != null) {
            if (hardCutCulling && batch.isEmpty()) {
                return;
            }

            batch.clear();
        }
    }

    private static void recoverEmptyCachedBatchForHardCut(RenderRegion region, TerrainRenderPass renderPass) {
        if (region == null) {
            return;
        }

        MultiDrawBatch batch = region.getCachedBatch(renderPass);
        if (batch != null && batch.isFilled && batch.isEmpty()) {
            batch.clear();
        }
    }

    private static boolean shouldHardCutSection(RenderRegion region, int sectionId) {
        if (region == null
                || !TeleportTransitionController.shouldHardCutFallbackTerrain()
                || TeleportTransitionController.shouldUseShaderScreenMaskOnly()) {
            return false;
        }

        int centerX = (region.getChunkX() + LocalSectionIndex.unpackX(sectionId)) * 16 + 8;
        int centerY = (region.getChunkY() + LocalSectionIndex.unpackY(sectionId)) * 16 + 8;
        int centerZ = (region.getChunkZ() + LocalSectionIndex.unpackZ(sectionId)) * 16 + 8;
        return TeleportTransitionController.shouldCullFallbackTerrainSection(centerX, centerY, centerZ);
    }

    private static int getChunkSectionFadeMillis() {
        double seconds = Minecraft.getInstance().options.chunkSectionFadeInTime().get();
        return Math.max(MIN_CHUNK_SECTION_FADE_MILLIS, (int) Math.round(seconds * 1000.0D));
    }

    private static UniformBufferManager getUniformBufferManager() {
        SodiumWorldRenderer renderer;
        try {
            renderer = SodiumWorldRenderer.instanceNullable();
        } catch (LinkageError error) {
            return null;
        }

        if (renderer == null) {
            return null;
        }

        try {
            Field field = getUniformBufferManagerField();
            Object value = field.get(renderer);
            if (value instanceof UniformBufferManager uniformBufferManager) {
                return uniformBufferManager;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }

        return null;
    }

    private static Field getUniformBufferManagerField() throws ReflectiveOperationException {
        if (gtalikeTeleport$uniformBufferManagerField == null) {
            Field field = SodiumWorldRenderer.class.getDeclaredField("uniformBufferManager");
            field.setAccessible(true);
            gtalikeTeleport$uniformBufferManagerField = field;
        }

        return gtalikeTeleport$uniformBufferManagerField;
    }
}