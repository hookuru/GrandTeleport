package dev.codex.gtaliketeleport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class VoxyCompat {
    private static final String CONFIG_CLASS = "me.cortex.voxy.client.config.VoxyConfig";

    private VoxyCompat() {
    }

    static boolean isRenderingEnabled() {
        try {
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Field configField = configClass.getField("CONFIG");
            Object config = configField.get(null);
            if (config == null) {
                return false;
            }

            Method method = configClass.getMethod("isRenderingEnabled");
            return Boolean.TRUE.equals(method.invoke(config));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return true;
        }
    }
}
