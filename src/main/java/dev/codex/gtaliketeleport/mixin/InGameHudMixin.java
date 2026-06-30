package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportStepEffectRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void gtalikeTeleport$renderEffectAfterHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        TeleportStepEffectRenderer.render(context, tickCounter.getTickProgress(false));
    }
}
