package dev.codex.gtaliketeleport.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$hideFallbackSkyCelestials(
            PoseStack poseStack,
            float sunAngle,
            float moonAngle,
            float starAngle,
            MoonPhase moonPhase,
            float rainBrightness,
            float starBrightness,
            CallbackInfo ci
    ) {
        if (TeleportTransitionController.shouldHideFallbackSkyCelestials()) {
            ci.cancel();
        }
    }
}
