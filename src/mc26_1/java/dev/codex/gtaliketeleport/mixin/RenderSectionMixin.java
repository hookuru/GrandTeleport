package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class RenderSectionMixin {
    @Shadow
    public abstract BlockPos getRenderOrigin();

    @Inject(method = "getVisibility", at = @At("RETURN"), cancellable = true)
    private void gtalikeTeleport$applyFallbackTerrainVisibility(long now, CallbackInfoReturnable<Float> cir) {
        if (!TeleportTransitionController.shouldApplyVanillaFallbackTerrainVisibility()) {
            return;
        }

        float visibility = TeleportTransitionController.getFallbackTerrainSectionVisibility(this.getRenderOrigin());
        if (visibility >= 1.0F) {
            return;
        }

        cir.setReturnValue(cir.getReturnValue() * visibility);
    }
}
