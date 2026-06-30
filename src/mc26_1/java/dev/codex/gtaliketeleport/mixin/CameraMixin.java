package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "update", at = @At("TAIL"))
    private void gtalikeTeleport$overrideCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
        TeleportTransitionController.CameraFrame frame = TeleportTransitionController.getCameraFrame(deltaTracker.getGameTimeDeltaPartialTick(false));
        if (frame == null) {
            return;
        }

        CameraAccessor accessor = (CameraAccessor) this;
        accessor.gtalikeTeleport$setPosition(frame.pos());
        accessor.gtalikeTeleport$setRotation(frame.yaw(), frame.pitch());
        accessor.gtalikeTeleport$prepareCullFrustum(
                ((Camera) (Object) this).getViewRotationMatrix(new Matrix4f()),
                accessor.gtalikeTeleport$createProjectionMatrixForCulling(),
                frame.pos());
        TeleportTransitionController.requestTerrainVisibilityUpdate(frame.pos());
    }
}
