package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.GtaLikeTeleportServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$delayExternalTeleport(
            ServerLevel level,
            double x,
            double y,
            double z,
            Set<Relative> relatives,
            float yaw,
            float pitch,
            boolean resetCamera,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (GtaLikeTeleportServer.tryDelayExternalTeleport((ServerPlayer) (Object) this, level, x, y, z, relatives, yaw, pitch, resetCamera)) {
            cir.setReturnValue(true);
        }
    }
}
