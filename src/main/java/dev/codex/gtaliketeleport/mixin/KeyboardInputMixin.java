package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$blockMovementInput(CallbackInfo ci) {
        if (!TeleportTransitionController.shouldBlockPlayerInput()) {
            return;
        }

        this.playerInput = PlayerInput.DEFAULT;
        this.movementVector = Vec2f.ZERO;
        ci.cancel();
    }
}