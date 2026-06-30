package dev.codex.gtaliketeleport;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class GtaLikeTeleportModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GtaLikeTeleportConfigScreen::new;
    }
}