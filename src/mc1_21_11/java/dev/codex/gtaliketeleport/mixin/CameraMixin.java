package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "setup", at = @At("TAIL"))
    private void gtalikeTeleport$overrideCamera(Level level, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        TeleportTransitionController.CameraFrame frame = TeleportTransitionController.getCameraFrame(tickProgress);
        if (frame == null) {
            return;
        }

        CameraAccessor accessor = (CameraAccessor) this;
        accessor.gtalikeTeleport$setPosition(frame.pos());
        accessor.gtalikeTeleport$setRotation(frame.yaw(), frame.pitch());
        TeleportTransitionController.requestTerrainVisibilityUpdate(frame.pos());
    }
}