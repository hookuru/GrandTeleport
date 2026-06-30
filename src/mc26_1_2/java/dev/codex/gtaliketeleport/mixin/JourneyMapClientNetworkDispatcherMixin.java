package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.GtaLikeTeleportClient;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(targets = "journeymap.common.network.dispatch.ClientNetworkDispatcher", remap = false)
public abstract class JourneyMapClientNetworkDispatcherMixin {
    @Inject(method = "sendTeleportPacket(DIDLjava/lang/String;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtalikeTeleport$interceptJourneyMapTeleport(double x, int y, double z, String dimension, CallbackInfo ci) {
        Vec3 targetFeet = new Vec3(x, y, z);
        Runnable action = () -> invokeJourneyMapTeleport(
                new Class<?>[]{double.class, int.class, double.class, String.class},
                x,
                y,
                z,
                dimension
        );

        if (!GtaLikeTeleportClient.interceptJourneyMapTeleport(targetFeet, dimension, action)) {
            ci.cancel();
        }
    }

    @Inject(method = "sendTeleportPacket(DIDLjava/lang/String;Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtalikeTeleport$interceptJourneyMapTeleportWithWaypoint(double x, int y, double z, String dimension, String waypointId, CallbackInfo ci) {
        Vec3 targetFeet = new Vec3(x, y, z);
        Runnable action = () -> invokeJourneyMapTeleport(
                new Class<?>[]{double.class, int.class, double.class, String.class, String.class},
                x,
                y,
                z,
                dimension,
                waypointId
        );

        if (!GtaLikeTeleportClient.interceptJourneyMapTeleport(targetFeet, dimension, action)) {
            ci.cancel();
        }
    }

    private void invokeJourneyMapTeleport(Class<?>[] parameterTypes, Object... args) {
        try {
            getClass().getMethod("sendTeleportPacket", parameterTypes).invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }
    }
}
