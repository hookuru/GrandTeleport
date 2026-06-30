package dev.codex.gtaliketeleport.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.codex.gtaliketeleport.TeleportTransitionController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void gtalikeTeleport$hideLocalPlayerDuringBodyCamera(
            E entity,
            double x,
            double y,
            double z,
            float yaw,
            float tickProgress,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CallbackInfo ci
    ) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && entity.getId() == player.getId() && TeleportTransitionController.shouldHideLocalPlayerModel()) {
            ci.cancel();
        }
    }
}
