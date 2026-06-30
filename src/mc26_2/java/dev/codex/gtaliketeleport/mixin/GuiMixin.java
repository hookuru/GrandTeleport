package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportStepEffectRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private GuiRenderState guiRenderState;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void gtalikeTeleport$renderEffectAfterHud(DeltaTracker deltaTracker, boolean renderLevel, boolean renderGui, CallbackInfo ci) {
        GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(Minecraft.getInstance(), this.guiRenderState, 0, 0);
        TeleportStepEffectRenderer.render(graphics, deltaTracker);
    }
}