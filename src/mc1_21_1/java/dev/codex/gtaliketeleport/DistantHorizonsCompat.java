package dev.codex.gtaliketeleport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class DistantHorizonsCompat {
    private static final String CLIENT_CONFIG_CLASS = "com.seibel.distanthorizons.core.config.Config$Client";
    private static final String CONFIG_ENTRY_CLASS = "com.seibel.distanthorizons.core.config.types.ConfigEntry";

    private DistantHorizonsCompat() {
    }

    static boolean isRenderingEnabled() {
        try {
            Class<?> configClass = Class.forName(CLIENT_CONFIG_CLASS);
            Field field = configClass.getField("quickEnableRendering");
            Object entry = field.get(null);
            if (entry == null) {
                return false;
            }

            Method get = Class.forName(CONFIG_ENTRY_CLASS).getMethod("get");
            return Boolean.TRUE.equals(get.invoke(entry));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return true;
        }
    }
}