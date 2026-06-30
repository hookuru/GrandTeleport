package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private void applyFrustum(Frustum frustum) {
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void gtalikeTeleport$applyTransitionFrustum(Camera camera, CallbackInfo ci) {
        if (!TeleportTransitionController.shouldForceTerrainFrustumApply()) {
            return;
        }

        this.applyFrustum(LevelRenderer.offsetFrustum(camera.getCullFrustum()));
    }
}
