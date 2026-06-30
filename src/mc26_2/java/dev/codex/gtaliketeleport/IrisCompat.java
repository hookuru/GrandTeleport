package dev.codex.gtaliketeleport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class IrisCompat {
    private IrisCompat() {
    }

    static boolean shouldUseHardTerrainCut() {
        return isShaderPackInUse();
    }

    private static boolean isShaderPackInUse() {
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Method getInstance = apiClass.getMethod("getInstance");
            Object api = getInstance.invoke(null);
            Method isShaderPackInUse = apiClass.getMethod("isShaderPackInUse");
            return Boolean.TRUE.equals(isShaderPackInUse.invoke(api));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException | LinkageError ignored) {
            return false;
        }
    }

}
