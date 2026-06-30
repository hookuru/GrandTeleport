package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.GtaLikeTeleportClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void gtalikeTeleport$interceptCommand(String command, CallbackInfo ci) {
        if (!GtaLikeTeleportClient.interceptOutgoingCommand(command)) {
            ci.cancel();
        }
    }

    @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true, require = 0)
    private void gtalikeTeleport$interceptUnattendedCommand(String command, Screen screen, CallbackInfo ci) {
        if (!GtaLikeTeleportClient.interceptOutgoingCommand(command)) {
            ci.cancel();
        }
    }

}
