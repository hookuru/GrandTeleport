package dev.codex.gtaliketeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class WaystonesWarpPlateHandler {
    private WaystonesWarpPlateHandler() {
    }

    public static boolean handleWarpPlateTeleport(Object warpPlate, Entity entity, Object target, ItemStack stack) {
        if (!(entity instanceof ServerPlayer player) || warpPlate == null || target == null || !isValidWaystone(target)) {
            return false;
        }

        Method teleportMethod = findTeleportToTargetMethod(warpPlate, player, target, stack);
        BlockPos pos = getWaystonePos(target);
        if (teleportMethod == null || pos == null) {
            return false;
        }

        if (!GtaLikeTeleportConfig.isWarpPlateTransitionsEnabled()) {
            GtaLikeTeleportServer.markNextServerTeleportBypassed(player);
            return false;
        }

        Vec3 targetFeet = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        return GtaLikeTeleportServer.scheduleServerTransition(
                player,
                GtaLikeTeleportNetworkPayloads.SOURCE_WARP_PLATE,
                targetFeet,
                getWaystoneDimension(player, target),
                () -> runWarpPlateTeleport(warpPlate, teleportMethod, player, target, stack)
        );
    }

    private static boolean isValidWaystone(Object target) {
        try {
            Object result = target.getClass().getMethod("isValid").invoke(target);
            return !(result instanceof Boolean valid) || valid;
        } catch (ReflectiveOperationException ignored) {
            return true;
        }
    }

    private static BlockPos getWaystonePos(Object target) {
        try {
            Object result = target.getClass().getMethod("getPos").invoke(target);
            return result instanceof BlockPos pos ? pos : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static ResourceKey<Level> getWaystoneDimension(ServerPlayer player, Object target) {
        try {
            Object result = target.getClass().getMethod("getDimension").invoke(target);
            if (result instanceof ResourceKey<?> key) {
                @SuppressWarnings("unchecked")
                ResourceKey<Level> dimension = (ResourceKey<Level>) key;
                return dimension;
            }
        } catch (ReflectiveOperationException | ClassCastException ignored) {
        }
        return player.level().dimension();
    }

    private static Method findTeleportToTargetMethod(Object warpPlate, ServerPlayer player, Object target, ItemStack stack) {
        Class<?> current = warpPlate.getClass();
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (!method.getName().equals("teleportToTarget") || method.getParameterCount() != 3) {
                    continue;
                }
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters[0].isInstance(player) && parameters[1].isInstance(target) && parameters[2].isInstance(stack)) {
                    method.setAccessible(true);
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static void runWarpPlateTeleport(Object warpPlate, Method teleportMethod, ServerPlayer player, Object target, ItemStack stack) {
        GtaLikeTeleportServer.runWithServerTeleportBypass(player, () -> {
            try {
                teleportMethod.invoke(warpPlate, player, target, stack);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        });
    }
}