package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Redirect(
            method = "updateMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void gtalikeTeleport$blockMouseLook(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        if (!TeleportTransitionController.shouldBlockPlayerInput()) {
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$blockMousePress(long window, MouseInput input, int action, CallbackInfo ci) {
        if (action != 0 && TeleportTransitionController.shouldBlockPlayerInput()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$blockMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (TeleportTransitionController.shouldBlockPlayerInput()) {
            ci.cancel();
        }
    }
}