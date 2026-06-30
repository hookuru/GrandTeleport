package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(
            method = "finalizeRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)V",
            at = @At("TAIL")
    )
    private void gtalikeTeleport$clearHiddenLocalPlayerShadow(
            Entity entity,
            EntityRenderState state,
            CallbackInfo ci
    ) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || entity.getId() != player.getId() || !TeleportTransitionController.shouldHideLocalPlayerModel()) {
            return;
        }

        state.shadowRadius = 0.0F;
        state.shadowPieces.clear();
        state.nameTag = null;
    }
}