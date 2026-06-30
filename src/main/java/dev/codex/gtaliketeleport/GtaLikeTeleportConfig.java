package dev.codex.gtaliketeleport;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class GtaLikeTeleportConfig {
    private static final String FILE_NAME = "gtalike_teleport.properties";
    private static final String EFFECT_ENABLED_KEY = "effectEnabled";

    private static Path configPath;
    private static boolean effectEnabled = true;

    private GtaLikeTeleportConfig() {
    }

    static void load() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

        if (!Files.exists(configPath)) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
            effectEnabled = Boolean.parseBoolean(properties.getProperty(EFFECT_ENABLED_KEY, Boolean.toString(effectEnabled)));
        } catch (IOException ignored) {
            effectEnabled = true;
        }
    }

    static boolean isEffectEnabled() {
        return effectEnabled;
    }

    static boolean setEffectEnabled(boolean enabled) {
        effectEnabled = enabled;
        return save();
    }

    private static boolean save() {
        if (configPath == null) {
            configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        }

        Properties properties = new Properties();
        properties.setProperty(EFFECT_ENABLED_KEY, Boolean.toString(effectEnabled));

        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "GTA Like Teleport client settings");
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
