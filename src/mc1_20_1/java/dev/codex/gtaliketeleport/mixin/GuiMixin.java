package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportStepEffectRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void gtalikeTeleport$renderEffectAfterHud(GuiGraphics graphics, float tickProgress, CallbackInfo ci) {
        TeleportStepEffectRenderer.render(graphics, tickProgress);
    }
}
