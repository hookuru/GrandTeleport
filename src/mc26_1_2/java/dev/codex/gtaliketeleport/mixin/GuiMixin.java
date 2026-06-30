package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportStepEffectRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void gtalikeTeleport$renderEffectAfterHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        TeleportStepEffectRenderer.render(graphics, deltaTracker);
    }
}
