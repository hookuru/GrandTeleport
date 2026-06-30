package dev.codex.gtaliketeleport.mixin;

import dev.codex.gtaliketeleport.WaystonesWarpPlateHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity", remap = false)
public abstract class WaystonesWarpPlateBlockEntityMixin {
    @Inject(method = "teleportToTarget", at = @At("HEAD"), cancellable = true, remap = false)
    private void gtalikeTeleport$delayWarpPlateTeleport(Entity entity, @Coerce Object target, ItemStack stack, CallbackInfo ci) {
        if (WaystonesWarpPlateHandler.handleWarpPlateTeleport(this, entity, target, stack)) {
            ci.cancel();
        }
    }
}