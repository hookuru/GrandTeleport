package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportStepEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
    private void gtalikeTeleport$renderStepEffectBeforeGuiFlush(GuiGraphics graphics) {
        TeleportStepEffectRenderer.render(graphics, Minecraft.getInstance().getDeltaFrameTime());
        graphics.flush();
    }
}