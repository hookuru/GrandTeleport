package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$blockMovementInput(boolean slowDown, float movementMultiplier, CallbackInfo ci) {
        if (!TeleportTransitionController.shouldBlockPlayerInput()) {
            return;
        }

        this.leftImpulse = 0.0F;
        this.forwardImpulse = 0.0F;
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.jumping = false;
        this.shiftKeyDown = false;
        ci.cancel();
    }
}
