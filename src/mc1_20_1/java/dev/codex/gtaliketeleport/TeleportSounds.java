package dev.codex.gtaliketeleport;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

final class TeleportSounds {
    static final SoundEvent CAMERA_IN = create("teleport.camera_in");
    static final SoundEvent CAMERA_OUT = create("teleport.camera_out");
    static final SoundEvent TELEPORT = create("teleport.teleport");
    static final SoundEvent ZOOM_IN_LONG = create("teleport.zoom_in_long");
    static final SoundEvent ZOOM_IN_SHORT = create("teleport.zoom_in_short");
    static final SoundEvent ZOOM_OUT_LONG = create("teleport.zoom_out_long");
    static final SoundEvent ZOOM_OUT_SHORT = create("teleport.zoom_out_short");

    private TeleportSounds() {
    }

    private static SoundEvent create(String path) {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation("gtalike_teleport", path));
    }
}