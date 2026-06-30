package dev.codex.gtaliketeleport.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class GtaLikeTeleportMixinPlugin implements IMixinConfigPlugin {
    private static final String JOURNEYMAP_MIXIN = "dev.codex.gtaliketeleport.mixin.JourneyMapClientNetworkDispatcherMixin";
    private static final String VOXY_CLIENT_MIXIN = "dev.codex.gtaliketeleport.mixin.VoxyClientMixin";
    private static final String WAYSTONES_WARP_PLATE_MIXIN = "dev.codex.gtaliketeleport.mixin.WaystonesWarpPlateBlockEntityMixin";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals(JOURNEYMAP_MIXIN)) {
            return FabricLoader.getInstance().isModLoaded("journeymap");
        }

        if (mixinClassName.equals(VOXY_CLIENT_MIXIN)) {
            return FabricLoader.getInstance().isModLoaded("voxy");
        }

        if (mixinClassName.equals(WAYSTONES_WARP_PLATE_MIXIN)) {
            return FabricLoader.getInstance().isModLoaded("waystones");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
