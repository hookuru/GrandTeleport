package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.GtaLikeTeleportClient;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$interceptTeleportPacket(Packet<?> packet, PacketSendListener listener, CallbackInfo ci) {
        if (!GtaLikeTeleportClient.interceptOutgoingPacket((Connection) (Object) this, packet, listener)) {
            ci.cancel();
        }
    }
}
