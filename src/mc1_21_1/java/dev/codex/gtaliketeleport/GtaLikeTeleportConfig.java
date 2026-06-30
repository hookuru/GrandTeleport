package dev.codex.gtaliketeleport;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

final class GtaLikeTeleportConfig {
    private static final String FILE_NAME = "grand_teleport.properties";
    private static final String LEGACY_FILE_NAME = "gtalike_teleport.properties";
    private static final String EFFECT_ENABLED_KEY = "effectEnabled";
    private static final String PLAYER_FREEZE_ENABLED_KEY = "playerFreezeEnabled";
    private static final String CROSS_DIMENSION_TRAVEL_ENABLED_KEY = "crossDimensionTravelEnabled";
    private static final String ZOOM_HEIGHTS_LINKED_KEY = "zoomHeightsLinked";
    private static final String ZOOM_OUT_STAGE_KEY_PREFIX = "zoomOutStage";
    private static final String ZOOM_IN_STAGE_KEY_PREFIX = "zoomInStage";
    private static final String NETHER_ZOOM_HEIGHTS_LINKED_KEY = "netherZoomHeightsLinked";
    private static final String NETHER_ZOOM_OUT_STAGE_KEY_PREFIX = "netherZoomOutStage";
    private static final String NETHER_ZOOM_IN_STAGE_KEY_PREFIX = "netherZoomInStage";
    private static final String END_ZOOM_HEIGHTS_LINKED_KEY = "endZoomHeightsLinked";
    private static final String END_ZOOM_OUT_STAGE_KEY_PREFIX = "endZoomOutStage";
    private static final String END_ZOOM_IN_STAGE_KEY_PREFIX = "endZoomInStage";
    private static final String ZOOM_OUT_STAGE_TICKS_KEY_PREFIX = "zoomOutStageTicks";
    private static final String ZOOM_IN_STAGE_TICKS_KEY_PREFIX = "zoomInStageTicks";
    private static final String ZOOM_STAGE_GLIDE_HEIGHT_KEY = "zoomStageGlideHeight";
    private static final String ZOOM_STAGE_GLIDE_TICKS_KEY = "zoomStageGlideTicks";
    private static final String BODY_CAMERA_HEIGHT_KEY = "bodyCameraHeight";
    private static final String BODY_GLIDE_HEIGHT_KEY = "bodyGlideHeight";
    private static final String BODY_GLIDE_TICKS_KEY = "bodyGlideTicks";
    private static final String LOCAL_PLAYER_HIDE_TICKS_KEY = "localPlayerHideTicks";
    private static final String CUSTOM_SOUNDS_ENABLED_KEY = "customSoundsEnabled";
    private static final String MINECRAFT_SOUND_VOLUME_KEY = "minecraftSoundVolume";
    private static final String CUSTOM_SOUND_VOLUME_KEY = "customSoundVolume";
    private static final String WARP_PLATE_TRANSITIONS_ENABLED_KEY = "warpPlateTransitionsEnabled";
    private static final String EXTERNAL_TELEPORT_TRANSITIONS_ENABLED_KEY = "externalTeleportTransitionsEnabled";
    private static final String FALLBACK_CHUNK_FADE_ENABLED_KEY = "fallbackChunkFadeEnabled";
    private static final String CONFIG_LAYOUT_EDITOR_BUTTON_VISIBLE_KEY = "configLayoutEditorButtonVisible";
    private static final String CONFIG_LAYOUT_DEBUG_ENABLED_KEY = "configLayoutDebugEnabled";
    private static final String CONFIG_LAYOUT_ASPECT_LOCKED_KEY = "configLayoutAspectLocked";
    private static final String CONFIG_LAYOUT_GRID_ENABLED_KEY = "configLayoutGridEnabled";
    private static final String CONFIG_LAYOUT_SNAP_ENABLED_KEY = "configLayoutSnapEnabled";
    private static final String CONFIG_LAYOUT_CUSTOM_KEY = "configLayoutCustom";
    private static final String CONFIG_LAYOUT_X_KEY = "configLayoutX";
    private static final String CONFIG_LAYOUT_Y_KEY = "configLayoutY";
    private static final String CONFIG_LAYOUT_WIDTH_KEY = "configLayoutWidth";
    private static final String CONFIG_LAYOUT_HEIGHT_KEY = "configLayoutHeight";
    private static final String CONFIG_LAYOUT_BASE_WIDTH_KEY = "configLayoutBaseWidth";
    private static final String CONFIG_LAYOUT_BASE_HEIGHT_KEY = "configLayoutBaseHeight";
    private static final String CONFIG_WIDGET_PREFIX = "configWidget.";
    private static final String CONFIG_TEXT_PREFIX = "configText.";
    private static final String DEFAULT_CONFIG_PROPERTIES = """
bodyCameraHeight=6.0
bodyGlideHeight=0.5
bodyGlideTicks=10
configLayoutAspectLocked=false
configLayoutBaseHeight=353
configLayoutBaseWidth=640
configLayoutCustom=true
configLayoutDebugEnabled=false
configLayoutEditorButtonVisible=false
configLayoutGridEnabled=true
configLayoutHeight=0.6005665722379604
configLayoutSnapEnabled=false
configLayoutWidth=0.5796875
configLayoutX=0.2109375
configLayoutY=0.23796033994334279
configText.advanced1_title=GTP Advanced Settings (1)
configText.advanced2_description=Set tick lengths for each zoom stage. (1st / 2nd / 3rd)
configText.advanced2_title=ZoomStage Settings (2)
configText.advanced3_title=GTP Advanced Settings (3)
configText.done_button=Close
configText.fallback_chunk_fade_label=Vanilla/Sodium chunk-mask fade
configText.general_title=General Settings
configText.linked_slider=\\    camera_zoom 1st / 2nd / 3rd
configText.others_title=Other Settings
configText.reset_button=Reset
configText.sounds_title=Sound Settings
configText.title=ZoomStage Settings
configText.zoom_out_ticks_label=Zoom-out stage ticks
configWidget.advanced1_description.baseHeight=195
configWidget.advanced1_description.baseWidth=368
configWidget.advanced1_description.height=0.05128205128205128
configWidget.advanced1_description.width=0.5407608695652174
configWidget.advanced1_description.x=0.22826086956521738
configWidget.advanced1_description.y=0.13333333333333333
configWidget.advanced1_title.baseHeight=195
configWidget.advanced1_title.baseWidth=368
configWidget.advanced1_title.height=0.05128205128205128
configWidget.advanced1_title.width=0.24456521739130435
configWidget.advanced1_title.x=0.37771739130434784
configWidget.advanced1_title.y=0.015384615384615385
configWidget.advanced2_description.baseHeight=212
configWidget.advanced2_description.baseWidth=371
configWidget.advanced2_description.height=0.04716981132075472
configWidget.advanced2_description.width=0.7601078167115903
configWidget.advanced2_description.x=0.11859838274932614
configWidget.advanced2_description.y=0.15566037735849056
configWidget.advanced2_title.baseHeight=198
configWidget.advanced2_title.baseWidth=368
configWidget.advanced2_title.height=0.050505050505050504
configWidget.advanced2_title.width=0.24456521739130435
configWidget.advanced2_title.x=0.37771739130434784
configWidget.advanced2_title.y=0.020202020202020204
configWidget.advanced3_description.baseHeight=195
configWidget.advanced3_description.baseWidth=368
configWidget.advanced3_description.height=0.05128205128205128
configWidget.advanced3_description.width=0.6059782608695652
configWidget.advanced3_description.x=0.1956521739130435
configWidget.advanced3_description.y=0.13333333333333333
configWidget.advanced3_title.baseHeight=195
configWidget.advanced3_title.baseWidth=368
configWidget.advanced3_title.height=0.05128205128205128
configWidget.advanced3_title.width=0.24456521739130435
configWidget.advanced3_title.x=0.37771739130434784
configWidget.advanced3_title.y=0.015384615384615385
configWidget.body_glide_slider.baseHeight=195
configWidget.body_glide_slider.baseWidth=368
configWidget.body_glide_slider.height=0.24102564102564103
configWidget.body_glide_slider.width=0.5
configWidget.body_glide_slider.x=0.5
configWidget.body_glide_slider.y=0.2153846153846154
configWidget.body_glide_ticks_field.baseHeight=195
configWidget.body_glide_ticks_field.baseWidth=368
configWidget.body_glide_ticks_field.height=0.10256410256410256
configWidget.body_glide_ticks_field.width=0.25
configWidget.body_glide_ticks_field.x=0.7038043478260869
configWidget.body_glide_ticks_field.y=0.5025641025641026
configWidget.body_glide_ticks_label.baseHeight=195
configWidget.body_glide_ticks_label.baseWidth=368
configWidget.body_glide_ticks_label.height=0.07179487179487179
configWidget.body_glide_ticks_label.width=0.5652173913043478
configWidget.body_glide_ticks_label.x=0.043478260869565216
configWidget.body_glide_ticks_label.y=0.5333333333333333
configWidget.body_height_slider.baseHeight=195
configWidget.body_height_slider.baseWidth=368
configWidget.body_height_slider.height=0.24102564102564103
configWidget.body_height_slider.width=0.5
configWidget.body_height_slider.x=0.0
configWidget.body_height_slider.y=0.2153846153846154
configWidget.cross_dimension_travel_label.baseHeight=212
configWidget.cross_dimension_travel_label.baseWidth=371
configWidget.cross_dimension_travel_label.height=0.05660377358490566
configWidget.cross_dimension_travel_label.width=0.5417789757412399
configWidget.cross_dimension_travel_label.x=0.05390835579514825
configWidget.cross_dimension_travel_label.y=0.6132075471698113
configWidget.cross_dimension_travel_toggle.baseHeight=212
configWidget.cross_dimension_travel_toggle.baseWidth=371
configWidget.cross_dimension_travel_toggle.height=0.09433962264150944
configWidget.cross_dimension_travel_toggle.width=0.25067385444743934
configWidget.cross_dimension_travel_toggle.x=0.6954177897574124
configWidget.cross_dimension_travel_toggle.y=0.589622641509434
configWidget.custom_volume_slider.baseHeight=297
configWidget.custom_volume_slider.baseWidth=551
configWidget.custom_volume_slider.height=0.2222222222222222
configWidget.custom_volume_slider.width=0.5009074410163339
configWidget.custom_volume_slider.x=0.5009074410163339
configWidget.custom_volume_slider.y=0.4612794612794613
configWidget.description.baseHeight=212
configWidget.description.baseWidth=371
configWidget.description.height=0.05188679245283019
configWidget.description.width=0.6738544474393531
configWidget.description.x=0.16172506738544473
configWidget.description.y=0.15566037735849056
configWidget.dimension_end.baseHeight=198
configWidget.dimension_end.baseWidth=368
configWidget.dimension_end.height=0.08080808080808081
configWidget.dimension_end.width=0.043478260869565216
configWidget.dimension_end.x=0.5244565217391305
configWidget.dimension_end.y=0.8939393939393939
configWidget.dimension_nether.baseHeight=198
configWidget.dimension_nether.baseWidth=368
configWidget.dimension_nether.height=0.08080808080808081
configWidget.dimension_nether.width=0.043478260869565216
configWidget.dimension_nether.x=0.47554347826086957
configWidget.dimension_nether.y=0.8939393939393939
configWidget.dimension_overworld.baseHeight=198
configWidget.dimension_overworld.baseWidth=368
configWidget.dimension_overworld.height=0.08080808080808081
configWidget.dimension_overworld.width=0.043478260869565216
configWidget.dimension_overworld.x=0.4266304347826087
configWidget.dimension_overworld.y=0.8939393939393939
configWidget.done_button.baseHeight=198
configWidget.done_button.baseWidth=368
configWidget.done_button.height=0.10101010101010101
configWidget.done_button.width=0.44565217391304346
configWidget.done_button.x=0.5543478260869565
configWidget.done_button.y=0.7929292929292929
configWidget.effect_label.baseHeight=212
configWidget.effect_label.baseWidth=371
configWidget.effect_label.height=0.05188679245283019
configWidget.effect_label.width=0.31805929919137466
configWidget.effect_label.x=0.05390835579514825
configWidget.effect_label.y=0.3113207547169811
configWidget.effect_toggle.baseHeight=212
configWidget.effect_toggle.baseWidth=371
configWidget.effect_toggle.height=0.09433962264150944
configWidget.effect_toggle.width=0.25067385444743934
configWidget.effect_toggle.x=0.6954177897574124
configWidget.effect_toggle.y=0.28773584905660377
configWidget.external_teleport_label.baseHeight=198
configWidget.external_teleport_label.baseWidth=368
configWidget.external_teleport_label.height=0.045454545454545456
configWidget.external_teleport_label.width=0.483695652173913
configWidget.external_teleport_label.x=0.05434782608695652
configWidget.external_teleport_label.y=0.5707070707070707
configWidget.external_teleport_toggle.baseHeight=198
configWidget.external_teleport_toggle.baseWidth=368
configWidget.external_teleport_toggle.height=0.10101010101010101
configWidget.external_teleport_toggle.width=0.25
configWidget.external_teleport_toggle.x=0.6956521739130435
configWidget.external_teleport_toggle.y=0.5454545454545454
configWidget.fallback_chunk_fade_label.baseHeight=132
configWidget.fallback_chunk_fade_label.baseWidth=246
configWidget.fallback_chunk_fade_label.height=0.06060606060606061
configWidget.fallback_chunk_fade_label.width=0.483739837398374
configWidget.fallback_chunk_fade_label.x=0.052845528455284556
configWidget.fallback_chunk_fade_label.y=0.6287878787878788
configWidget.fallback_chunk_fade_toggle.baseHeight=198
configWidget.fallback_chunk_fade_toggle.baseWidth=368
configWidget.fallback_chunk_fade_toggle.height=0.10101010101010101
configWidget.fallback_chunk_fade_toggle.width=0.25
configWidget.fallback_chunk_fade_toggle.x=0.6956521739130435
configWidget.fallback_chunk_fade_toggle.y=0.601010101010101
configWidget.general_description.baseHeight=212
configWidget.general_description.baseWidth=371
configWidget.general_description.height=0.05188679245283019
configWidget.general_description.width=0.31266846361185985
configWidget.general_description.x=0.19137466307277629
configWidget.general_description.y=0.15566037735849056
configWidget.general_title.baseHeight=198
configWidget.general_title.baseWidth=368
configWidget.general_title.height=0.050505050505050504
configWidget.general_title.width=0.3125
configWidget.general_title.x=0.3451086956521739
configWidget.general_title.y=0.020202020202020204
configWidget.link_button.baseHeight=198
configWidget.link_button.baseWidth=368
configWidget.link_button.height=0.10101010101010101
configWidget.link_button.width=0.05434782608695652
configWidget.link_button.x=0.47282608695652173
configWidget.link_button.y=0.7929292929292929
configWidget.linked_slider.baseHeight=0
configWidget.linked_slider.baseWidth=0
configWidget.linked_slider.height=0.2571428571428571
configWidget.linked_slider.width=1.0
configWidget.linked_slider.x=0.0
configWidget.linked_slider.y=0.3314285714285714
configWidget.minecraft_volume_slider.baseHeight=297
configWidget.minecraft_volume_slider.baseWidth=551
configWidget.minecraft_volume_slider.height=0.2222222222222222
configWidget.minecraft_volume_slider.width=0.5009074410163339
configWidget.minecraft_volume_slider.x=0.0
configWidget.minecraft_volume_slider.y=0.4612794612794613
configWidget.movement_label.baseHeight=212
configWidget.movement_label.baseWidth=371
configWidget.movement_label.height=0.06132075471698113
configWidget.movement_label.width=0.4366576819407008
configWidget.movement_label.x=0.05390835579514825
configWidget.movement_label.y=0.46226415094339623
configWidget.movement_toggle.baseHeight=212
configWidget.movement_toggle.baseWidth=371
configWidget.movement_toggle.height=0.09433962264150944
configWidget.movement_toggle.width=0.25067385444743934
configWidget.movement_toggle.x=0.6954177897574124
configWidget.movement_toggle.y=0.4386792452830189
configWidget.others_description.baseHeight=212
configWidget.others_description.baseWidth=371
configWidget.others_description.height=0.04716981132075472
configWidget.others_description.width=0.8382749326145552
configWidget.others_description.x=0.08086253369272237
configWidget.others_description.y=0.15566037735849056
configWidget.others_title.baseHeight=198
configWidget.others_title.baseWidth=368
configWidget.others_title.height=0.050505050505050504
configWidget.others_title.width=0.2826086956521739
configWidget.others_title.x=0.358695652173913
configWidget.others_title.y=0.020202020202020204
configWidget.player_hide_label.baseHeight=195
configWidget.player_hide_label.baseWidth=368
configWidget.player_hide_label.height=0.06153846153846154
configWidget.player_hide_label.width=0.5652173913043478
configWidget.player_hide_label.x=0.043478260869565216
configWidget.player_hide_label.y=0.676923076923077
configWidget.player_hide_slider.baseHeight=195
configWidget.player_hide_slider.baseWidth=368
configWidget.player_hide_slider.height=0.22564102564102564
configWidget.player_hide_slider.width=0.8695652173913043
configWidget.player_hide_slider.x=0.021739130434782608
configWidget.player_hide_slider.y=0.5692307692307692
configWidget.player_hide_ticks_field.baseHeight=195
configWidget.player_hide_ticks_field.baseWidth=368
configWidget.player_hide_ticks_field.height=0.10256410256410256
configWidget.player_hide_ticks_field.width=0.25
configWidget.player_hide_ticks_field.x=0.7038043478260869
configWidget.player_hide_ticks_field.y=0.6461538461538462
configWidget.reset_button.baseHeight=198
configWidget.reset_button.baseWidth=368
configWidget.reset_button.height=0.10101010101010101
configWidget.reset_button.width=0.44565217391304346
configWidget.reset_button.x=0.0
configWidget.reset_button.y=0.7929292929292929
configWidget.sound_mode_label.baseHeight=198
configWidget.sound_mode_label.baseWidth=368
configWidget.sound_mode_label.height=0.05555555555555555
configWidget.sound_mode_label.width=0.483695652173913
configWidget.sound_mode_label.x=0.06521739130434782
configWidget.sound_mode_label.y=0.29797979797979796
configWidget.sound_mode_toggle.baseHeight=198
configWidget.sound_mode_toggle.baseWidth=368
configWidget.sound_mode_toggle.height=0.10101010101010101
configWidget.sound_mode_toggle.width=0.30434782608695654
configWidget.sound_mode_toggle.x=0.6331521739130435
configWidget.sound_mode_toggle.y=0.2727272727272727
configWidget.sounds_description.baseHeight=212
configWidget.sounds_description.baseWidth=371
configWidget.sounds_description.height=0.04716981132075472
configWidget.sounds_description.width=0.6522911051212938
configWidget.sounds_description.x=0.1752021563342318
configWidget.sounds_description.y=0.15566037735849056
configWidget.sounds_title.baseHeight=198
configWidget.sounds_title.baseWidth=368
configWidget.sounds_title.height=0.050505050505050504
configWidget.sounds_title.width=0.28804347826086957
configWidget.sounds_title.x=0.35597826086956524
configWidget.sounds_title.y=0.020202020202020204
configWidget.status_linked.baseHeight=195
configWidget.status_linked.baseWidth=368
configWidget.status_linked.height=0.041025641025641026
configWidget.status_linked.width=0.08695652173913043
configWidget.status_linked.x=0.45652173913043476
configWidget.status_linked.y=0.9179487179487179
configWidget.status_unlinked.baseHeight=195
configWidget.status_unlinked.baseWidth=368
configWidget.status_unlinked.height=0.041025641025641026
configWidget.status_unlinked.width=0.13043478260869565
configWidget.status_unlinked.x=0.43478260869565216
configWidget.status_unlinked.y=0.9179487179487179
configWidget.tab_general.baseHeight=212
configWidget.tab_general.baseWidth=371
configWidget.tab_general.height=0.09433962264150944
configWidget.tab_general.width=0.2183288409703504
configWidget.tab_general.x=-0.03773584905660377
configWidget.tab_general.y=-0.13679245283018868
configWidget.tab_others.baseHeight=212
configWidget.tab_others.baseWidth=371
configWidget.tab_others.height=0.09433962264150944
configWidget.tab_others.width=0.2183288409703504
configWidget.tab_others.x=0.8194070080862533
configWidget.tab_others.y=-0.13679245283018868
configWidget.tab_sounds.baseHeight=212
configWidget.tab_sounds.baseWidth=371
configWidget.tab_sounds.height=0.09433962264150944
configWidget.tab_sounds.width=0.2183288409703504
configWidget.tab_sounds.x=0.6091644204851752
configWidget.tab_sounds.y=-0.13679245283018868
configWidget.tab_zoom_stage.baseHeight=212
configWidget.tab_zoom_stage.baseWidth=371
configWidget.tab_zoom_stage.height=0.09433962264150944
configWidget.tab_zoom_stage.width=0.2183288409703504
configWidget.tab_zoom_stage.x=0.1778975741239892
configWidget.tab_zoom_stage.y=-0.13679245283018868
configWidget.tab_zoom_stage_2.baseHeight=212
configWidget.tab_zoom_stage_2.baseWidth=371
configWidget.tab_zoom_stage_2.height=0.09433962264150944
configWidget.tab_zoom_stage_2.width=0.2183288409703504
configWidget.tab_zoom_stage_2.x=0.3935309973045822
configWidget.tab_zoom_stage_2.y=-0.13679245283018868
configWidget.title.baseHeight=198
configWidget.title.baseWidth=368
configWidget.title.height=0.05555555555555555
configWidget.title.width=0.13043478260869565
configWidget.title.x=0.43478260869565216
configWidget.title.y=0.020202020202020204
configWidget.warp_plate_label.baseHeight=198
configWidget.warp_plate_label.baseWidth=368
configWidget.warp_plate_label.height=0.045454545454545456
configWidget.warp_plate_label.width=0.483695652173913
configWidget.warp_plate_label.x=0.05434782608695652
configWidget.warp_plate_label.y=0.35353535353535354
configWidget.warp_plate_toggle.baseHeight=198
configWidget.warp_plate_toggle.baseWidth=368
configWidget.warp_plate_toggle.height=0.10101010101010101
configWidget.warp_plate_toggle.width=0.25
configWidget.warp_plate_toggle.x=0.6956521739130435
configWidget.warp_plate_toggle.y=0.3282828282828283
configWidget.zoom_in_slider.baseHeight=198
configWidget.zoom_in_slider.baseWidth=368
configWidget.zoom_in_slider.height=0.2222222222222222
configWidget.zoom_in_slider.width=1.0
configWidget.zoom_in_slider.x=0.0
configWidget.zoom_in_slider.y=0.5050505050505051
configWidget.zoom_in_ticks_field.baseHeight=198
configWidget.zoom_in_ticks_field.baseWidth=368
configWidget.zoom_in_ticks_field.height=0.10101010101010101
configWidget.zoom_in_ticks_field.width=0.37228260869565216
configWidget.zoom_in_ticks_field.x=0.5760869565217391
configWidget.zoom_in_ticks_field.y=0.5454545454545454
configWidget.zoom_in_ticks_label.baseHeight=198
configWidget.zoom_in_ticks_label.baseWidth=368
configWidget.zoom_in_ticks_label.height=0.06060606060606061
configWidget.zoom_in_ticks_label.width=0.42934782608695654
configWidget.zoom_in_ticks_label.x=0.05434782608695652
configWidget.zoom_in_ticks_label.y=0.5707070707070707
configWidget.zoom_out_slider.baseHeight=195
configWidget.zoom_out_slider.baseWidth=368
configWidget.zoom_out_slider.height=0.22564102564102564
configWidget.zoom_out_slider.width=1.0
configWidget.zoom_out_slider.x=0.0
configWidget.zoom_out_slider.y=0.23076923076923078
configWidget.zoom_out_ticks_field.baseHeight=198
configWidget.zoom_out_ticks_field.baseWidth=368
configWidget.zoom_out_ticks_field.height=0.10101010101010101
configWidget.zoom_out_ticks_field.width=0.37228260869565216
configWidget.zoom_out_ticks_field.x=0.5760869565217391
configWidget.zoom_out_ticks_field.y=0.3282828282828283
configWidget.zoom_out_ticks_label.baseHeight=198
configWidget.zoom_out_ticks_label.baseWidth=368
configWidget.zoom_out_ticks_label.height=0.06060606060606061
configWidget.zoom_out_ticks_label.width=0.42934782608695654
configWidget.zoom_out_ticks_label.x=0.05434782608695652
configWidget.zoom_out_ticks_label.y=0.35353535353535354
configWidget.zoom_stage_glide_slider.baseHeight=195
configWidget.zoom_stage_glide_slider.baseWidth=368
configWidget.zoom_stage_glide_slider.height=0.22564102564102564
configWidget.zoom_stage_glide_slider.width=1.0
configWidget.zoom_stage_glide_slider.x=0.0
configWidget.zoom_stage_glide_slider.y=0.26153846153846155
configWidget.zoom_stage_glide_ticks_field.baseHeight=195
configWidget.zoom_stage_glide_ticks_field.baseWidth=368
configWidget.zoom_stage_glide_ticks_field.height=0.10256410256410256
configWidget.zoom_stage_glide_ticks_field.width=0.25
configWidget.zoom_stage_glide_ticks_field.x=0.7092391304347826
configWidget.zoom_stage_glide_ticks_field.y=0.5897435897435898
configWidget.zoom_stage_glide_ticks_label.baseHeight=195
configWidget.zoom_stage_glide_ticks_label.baseWidth=368
configWidget.zoom_stage_glide_ticks_label.height=0.06666666666666667
configWidget.zoom_stage_glide_ticks_label.width=0.5652173913043478
configWidget.zoom_stage_glide_ticks_label.x=0.03804347826086957
configWidget.zoom_stage_glide_ticks_label.y=0.6153846153846154
crossDimensionTravelEnabled=false
customSoundVolume=0.5
customSoundsEnabled=false
effectEnabled=true
endZoomHeightsLinked=true
endZoomInStage1=20
endZoomInStage2=40
endZoomInStage3=60
endZoomOutStage1=20
endZoomOutStage2=40
endZoomOutStage3=60
externalTeleportTransitionsEnabled=true
fallbackChunkFadeEnabled=false
localPlayerHideTicks=2
minecraftSoundVolume=0.5
netherZoomHeightsLinked=true
netherZoomInStage1=20
netherZoomInStage2=40
netherZoomInStage3=60
netherZoomOutStage1=20
netherZoomOutStage2=40
netherZoomOutStage3=60
playerFreezeEnabled=true
warpPlateTransitionsEnabled=true
zoomHeightsLinked=true
zoomInStage1=20
zoomInStage2=40
zoomInStage3=60
zoomInStageTicks1=13
zoomInStageTicks2=13
zoomInStageTicks3=13
zoomOutStage1=20
zoomOutStage2=40
zoomOutStage3=60
zoomOutStageTicks1=13
zoomOutStageTicks2=13
zoomOutStageTicks3=13
zoomStageGlideHeight=0.5
zoomStageGlideTicks=13
            """;

    private static final double[] DEFAULT_STAGE_HEIGHTS = {20.0D, 40.0D, 60.0D};
    private static final double MIN_STAGE_HEIGHT = 8.0D;
    private static final double MAX_STAGE_HEIGHT = 512.0D;
    private static final double MIN_STAGE_GAP = 1.0D;
    private static final int[] DEFAULT_STAGE_TICKS = {13, 13, 13};
    private static final int MIN_STAGE_TICKS = 1;
    private static final int MAX_STAGE_TICKS = 200;
    private static final double DEFAULT_ZOOM_STAGE_GLIDE_HEIGHT = 0.5D;
    private static final double MIN_ZOOM_STAGE_GLIDE_HEIGHT = 0.1D;
    private static final double MAX_ZOOM_STAGE_GLIDE_HEIGHT = 5.0D;
    private static final int DEFAULT_ZOOM_STAGE_GLIDE_TICKS = 13;
    private static final double DEFAULT_BODY_CAMERA_HEIGHT = 6.0D;
    private static final double MIN_BODY_CAMERA_HEIGHT = 0.1D;
    private static final double MAX_BODY_CAMERA_HEIGHT = 10.0D;
    private static final double DEFAULT_BODY_GLIDE_HEIGHT = 0.5D;
    private static final double MIN_BODY_GLIDE_HEIGHT = 0.1D;
    private static final double MAX_BODY_GLIDE_HEIGHT = 5.0D;
    private static final int DEFAULT_BODY_GLIDE_TICKS = 10;
    private static final int MIN_LOCAL_PLAYER_HIDE_TICKS = 0;
    private static final int MAX_LOCAL_PLAYER_HIDE_TICKS = 20;
    private static final int DEFAULT_LOCAL_PLAYER_HIDE_TICKS = 2;
    private static final double DEFAULT_MINECRAFT_SOUND_VOLUME = 1.0D;
    private static final double DEFAULT_CUSTOM_SOUND_VOLUME = 0.3D;
    private static final double MIN_SOUND_VOLUME = 0.1D;
    private static final double MAX_SOUND_VOLUME = 1.0D;

    private static Path configPath;
    private static boolean effectEnabled = true;
    private static boolean playerFreezeEnabled = true;
    private static boolean crossDimensionTravelEnabled = false;
    private static boolean zoomHeightsLinked = true;
    private static double[] zoomOutStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static double[] zoomInStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static boolean netherZoomHeightsLinked = true;
    private static double[] netherZoomOutStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static double[] netherZoomInStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static boolean endZoomHeightsLinked = true;
    private static double[] endZoomOutStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static double[] endZoomInStageHeights = DEFAULT_STAGE_HEIGHTS.clone();
    private static int[] zoomOutStageTicks = DEFAULT_STAGE_TICKS.clone();
    private static int[] zoomInStageTicks = DEFAULT_STAGE_TICKS.clone();
    private static double zoomStageGlideHeight = DEFAULT_ZOOM_STAGE_GLIDE_HEIGHT;
    private static int zoomStageGlideTicks = DEFAULT_ZOOM_STAGE_GLIDE_TICKS;
    private static double bodyCameraHeight = DEFAULT_BODY_CAMERA_HEIGHT;
    private static double bodyGlideHeight = DEFAULT_BODY_GLIDE_HEIGHT;
    private static int bodyGlideTicks = DEFAULT_BODY_GLIDE_TICKS;
    private static int localPlayerHideTicks = DEFAULT_LOCAL_PLAYER_HIDE_TICKS;
    private static boolean customSoundsEnabled = false;
    private static double minecraftSoundVolume = DEFAULT_MINECRAFT_SOUND_VOLUME;
    private static double customSoundVolume = DEFAULT_CUSTOM_SOUND_VOLUME;
    private static boolean warpPlateTransitionsEnabled = true;
    private static boolean externalTeleportTransitionsEnabled = true;
    private static boolean fallbackChunkFadeEnabled = false;
    private static boolean configLayoutEditorButtonVisible = false;
    private static boolean configLayoutDebugEnabled = false;
    private static boolean configLayoutAspectLocked = true;
    private static boolean configLayoutGridEnabled = true;
    private static boolean configLayoutSnapEnabled = true;
    private static boolean configLayoutCustom = false;
    private static double configLayoutX = 0.0D;
    private static double configLayoutY = 0.0D;
    private static double configLayoutWidth = 0.0D;
    private static double configLayoutHeight = 0.0D;
    private static int configLayoutBaseWidth = 0;
    private static int configLayoutBaseHeight = 0;
    private static final Map<String, double[]> configWidgetLayouts = new HashMap<>();
    private static final Map<String, String> configTexts = new HashMap<>();

    private GtaLikeTeleportConfig() {
    }

    static void load() {
        configPath = resolveConfigPath();
        migrateLegacyConfig();
        resetToDefaults();

        if (!Files.exists(configPath)) {
            return;
        }

        boolean rewriteConfig = false;
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
            rewriteConfig = prepareLoadedProperties(properties);
            applyConfigProperties(properties);
            rewriteConfig = rewriteConfig || !properties.containsKey(CONFIG_LAYOUT_EDITOR_BUTTON_VISIBLE_KEY);
        } catch (IOException ignored) {
            resetToDefaults();
        }
        if (rewriteConfig) {
            save();
        }
    }

    static boolean isEffectEnabled() {
        return effectEnabled;
    }

    static boolean setEffectEnabled(boolean enabled) {
        effectEnabled = enabled;
        return save();
    }

    static boolean isPlayerFreezeEnabled() {
        return playerFreezeEnabled;
    }

    static boolean setPlayerFreezeEnabled(boolean enabled) {
        playerFreezeEnabled = enabled;
        return save();
    }

    static boolean isCrossDimensionTravelEnabled() {
        return crossDimensionTravelEnabled;
    }

    static boolean setCrossDimensionTravelEnabled(boolean enabled) {
        crossDimensionTravelEnabled = enabled;
        return save();
    }

    static boolean areZoomHeightsLinked() {
        return areZoomHeightsLinked(ZoomDimension.OVERWORLD);
    }

    static boolean areZoomHeightsLinked(ZoomDimension dimension) {
        return switch (sanitizeZoomDimension(dimension)) {
            case NETHER -> netherZoomHeightsLinked;
            case END -> endZoomHeightsLinked;
            default -> zoomHeightsLinked;
        };
    }

    static double[] getZoomOutStageHeights() {
        return getZoomOutStageHeights(ZoomDimension.OVERWORLD);
    }

    static double[] getZoomOutStageHeights(ZoomDimension dimension) {
        return switch (sanitizeZoomDimension(dimension)) {
            case NETHER -> netherZoomOutStageHeights.clone();
            case END -> endZoomOutStageHeights.clone();
            default -> zoomOutStageHeights.clone();
        };
    }

    static double[] getZoomInStageHeights() {
        return getZoomInStageHeights(ZoomDimension.OVERWORLD);
    }

    static double[] getZoomInStageHeights(ZoomDimension dimension) {
        ZoomDimension safeDimension = sanitizeZoomDimension(dimension);
        return areZoomHeightsLinked(safeDimension) ? getZoomOutStageHeights(safeDimension) : getRawZoomInStageHeights(safeDimension);
    }

    static double[] getRawZoomInStageHeights() {
        return getRawZoomInStageHeights(ZoomDimension.OVERWORLD);
    }

    static double[] getRawZoomInStageHeights(ZoomDimension dimension) {
        return switch (sanitizeZoomDimension(dimension)) {
            case NETHER -> netherZoomInStageHeights.clone();
            case END -> endZoomInStageHeights.clone();
            default -> zoomInStageHeights.clone();
        };
    }

    static boolean setZoomStageHeights(boolean linked, double[] zoomOutHeights, double[] zoomInHeights) {
        return setZoomStageHeights(ZoomDimension.OVERWORLD, linked, zoomOutHeights, zoomInHeights);
    }

    static boolean setZoomStageHeights(ZoomDimension dimension, boolean linked, double[] zoomOutHeights, double[] zoomInHeights) {
        double[] sanitizedOut = sanitizeStageHeights(zoomOutHeights);
        double[] sanitizedIn = sanitizeStageHeights(linked ? sanitizedOut : zoomInHeights);
        switch (sanitizeZoomDimension(dimension)) {
            case NETHER -> {
                netherZoomHeightsLinked = linked;
                netherZoomOutStageHeights = sanitizedOut;
                netherZoomInStageHeights = sanitizedIn;
            }
            case END -> {
                endZoomHeightsLinked = linked;
                endZoomOutStageHeights = sanitizedOut;
                endZoomInStageHeights = sanitizedIn;
            }
            default -> {
                zoomHeightsLinked = linked;
                zoomOutStageHeights = sanitizedOut;
                zoomInStageHeights = sanitizedIn;
            }
        }
        return save();
    }

    static int[] getZoomOutStageTicks() {
        return zoomOutStageTicks.clone();
    }

    static int[] getZoomInStageTicks() {
        return zoomInStageTicks.clone();
    }

    static boolean setZoomStageTicks(int[] zoomOutTicks, int[] zoomInTicks) {
        zoomOutStageTicks = sanitizeStageTicks(zoomOutTicks);
        zoomInStageTicks = sanitizeStageTicks(zoomInTicks);
        return save();
    }

    static double getZoomStageGlideHeight() {
        return zoomStageGlideHeight;
    }

    static boolean setZoomStageGlideHeight(double height) {
        zoomStageGlideHeight = sanitizeZoomStageGlideHeight(height);
        return save();
    }

    static int getZoomStageGlideTicks() {
        return zoomStageGlideTicks;
    }

    static boolean setZoomStageGlideTicks(int ticks) {
        zoomStageGlideTicks = sanitizeStageTicksValue(ticks);
        return save();
    }

    static double getBodyCameraHeight() {
        return bodyCameraHeight;
    }

    static boolean setBodyCameraHeight(double height) {
        bodyCameraHeight = sanitizeBodyCameraHeight(height);
        return save();
    }

    static double getBodyGlideHeight() {
        return bodyGlideHeight;
    }

    static boolean setBodyGlideHeight(double height) {
        bodyGlideHeight = sanitizeBodyGlideHeight(height);
        return save();
    }

    static int getBodyGlideTicks() {
        return bodyGlideTicks;
    }

    static boolean setBodyGlideTicks(int ticks) {
        bodyGlideTicks = sanitizeStageTicksValue(ticks);
        return save();
    }

    static int getLocalPlayerHideTicks() {
        return localPlayerHideTicks;
    }

    static boolean setLocalPlayerHideTicks(int ticks) {
        localPlayerHideTicks = sanitizeLocalPlayerHideTicks(ticks);
        return save();
    }

    static boolean isCustomSoundsEnabled() {
        return customSoundsEnabled;
    }

    static boolean setCustomSoundsEnabled(boolean enabled) {
        customSoundsEnabled = enabled;
        return save();
    }

    static double getMinecraftSoundVolume() {
        return minecraftSoundVolume;
    }

    static boolean setMinecraftSoundVolume(double volume) {
        minecraftSoundVolume = sanitizeSoundVolume(volume);
        return save();
    }

    static double getCustomSoundVolume() {
        return customSoundVolume;
    }

    static boolean setCustomSoundVolume(double volume) {
        customSoundVolume = sanitizeSoundVolume(volume);
        return save();
    }

    static boolean isWarpPlateTransitionsEnabled() {
        return warpPlateTransitionsEnabled;
    }

    static boolean setWarpPlateTransitionsEnabled(boolean enabled) {
        warpPlateTransitionsEnabled = enabled;
        return save();
    }

    static boolean isExternalTeleportTransitionsEnabled() {
        return externalTeleportTransitionsEnabled;
    }

    static boolean setExternalTeleportTransitionsEnabled(boolean enabled) {
        externalTeleportTransitionsEnabled = enabled;
        return save();
    }

    static boolean isFallbackChunkFadeEnabled() {
        return fallbackChunkFadeEnabled;
    }

    static boolean setFallbackChunkFadeEnabled(boolean enabled) {
        fallbackChunkFadeEnabled = enabled;
        return save();
    }

    static double[] sanitizeStageHeights(double[] values) {
        double[] source = values == null || values.length < 3 ? DEFAULT_STAGE_HEIGHTS : values;
        double[] sanitized = new double[3];
        sanitized[0] = clamp(roundStageHeight(source[0]), MIN_STAGE_HEIGHT, MAX_STAGE_HEIGHT - MIN_STAGE_GAP * 2.0D);
        sanitized[1] = clamp(roundStageHeight(source[1]), sanitized[0] + MIN_STAGE_GAP, MAX_STAGE_HEIGHT - MIN_STAGE_GAP);
        sanitized[2] = clamp(roundStageHeight(source[2]), sanitized[1] + MIN_STAGE_GAP, MAX_STAGE_HEIGHT);
        return sanitized;
    }

    static int[] sanitizeStageTicks(int[] values) {
        int[] source = values == null || values.length < 3 ? DEFAULT_STAGE_TICKS : values;
        int[] sanitized = new int[3];
        for (int i = 0; i < sanitized.length; i++) {
            sanitized[i] = sanitizeStageTicksValue(source[i]);
        }
        return sanitized;
    }

    static double sanitizeZoomStageGlideHeight(double value) {
        return Math.round(clamp(value, MIN_ZOOM_STAGE_GLIDE_HEIGHT, MAX_ZOOM_STAGE_GLIDE_HEIGHT) * 10.0D) / 10.0D;
    }

    static double sanitizeBodyCameraHeight(double value) {
        return Math.round(clamp(value, MIN_BODY_CAMERA_HEIGHT, MAX_BODY_CAMERA_HEIGHT) * 10.0D) / 10.0D;
    }

    static double sanitizeBodyGlideHeight(double value) {
        return Math.round(clamp(value, MIN_BODY_GLIDE_HEIGHT, MAX_BODY_GLIDE_HEIGHT) * 10.0D) / 10.0D;
    }

    static int sanitizeStageTicksValue(int value) {
        return clamp(value, MIN_STAGE_TICKS, MAX_STAGE_TICKS);
    }

    static int sanitizeLocalPlayerHideTicks(int value) {
        return clamp(value, MIN_LOCAL_PLAYER_HIDE_TICKS, MAX_LOCAL_PLAYER_HIDE_TICKS);
    }

    static double sanitizeSoundVolume(double value) {
        return Math.round(clamp(value, MIN_SOUND_VOLUME, MAX_SOUND_VOLUME) * 10.0D) / 10.0D;
    }

    static double getMinStageHeight() {
        return MIN_STAGE_HEIGHT;
    }

    static double getMaxStageHeight() {
        return MAX_STAGE_HEIGHT;
    }

    static double getMinStageGap() {
        return MIN_STAGE_GAP;
    }

    static double[] getDefaultStageHeights() {
        return DEFAULT_STAGE_HEIGHTS.clone();
    }

    static int[] getDefaultStageTicks() {
        return DEFAULT_STAGE_TICKS.clone();
    }

    static int getMinStageTicks() {
        return MIN_STAGE_TICKS;
    }

    static int getMaxStageTicks() {
        return MAX_STAGE_TICKS;
    }

    static double getDefaultZoomStageGlideHeight() {
        return DEFAULT_ZOOM_STAGE_GLIDE_HEIGHT;
    }

    static double getMinZoomStageGlideHeight() {
        return MIN_ZOOM_STAGE_GLIDE_HEIGHT;
    }

    static double getMaxZoomStageGlideHeight() {
        return MAX_ZOOM_STAGE_GLIDE_HEIGHT;
    }

    static int getDefaultZoomStageGlideTicks() {
        return DEFAULT_ZOOM_STAGE_GLIDE_TICKS;
    }

    static double getDefaultBodyCameraHeight() {
        return DEFAULT_BODY_CAMERA_HEIGHT;
    }

    static double getMinBodyCameraHeight() {
        return MIN_BODY_CAMERA_HEIGHT;
    }

    static double getMaxBodyCameraHeight() {
        return MAX_BODY_CAMERA_HEIGHT;
    }

    static double getDefaultBodyGlideHeight() {
        return DEFAULT_BODY_GLIDE_HEIGHT;
    }

    static double getMinBodyGlideHeight() {
        return MIN_BODY_GLIDE_HEIGHT;
    }

    static double getMaxBodyGlideHeight() {
        return MAX_BODY_GLIDE_HEIGHT;
    }

    static int getDefaultBodyGlideTicks() {
        return DEFAULT_BODY_GLIDE_TICKS;
    }

    static int getDefaultLocalPlayerHideTicks() {
        return DEFAULT_LOCAL_PLAYER_HIDE_TICKS;
    }

    static int getMinLocalPlayerHideTicks() {
        return MIN_LOCAL_PLAYER_HIDE_TICKS;
    }

    static int getMaxLocalPlayerHideTicks() {
        return MAX_LOCAL_PLAYER_HIDE_TICKS;
    }

    static double getDefaultMinecraftSoundVolume() {
        return DEFAULT_MINECRAFT_SOUND_VOLUME;
    }

    static double getDefaultCustomSoundVolume() {
        return DEFAULT_CUSTOM_SOUND_VOLUME;
    }

    static double getMinSoundVolume() {
        return MIN_SOUND_VOLUME;
    }

    static double getMaxSoundVolume() {
        return MAX_SOUND_VOLUME;
    }

    static boolean isConfigLayoutEditorButtonVisible() {
        return configLayoutEditorButtonVisible;
    }

    static boolean setConfigLayoutEditorButtonVisible(boolean visible) {
        configLayoutEditorButtonVisible = visible;
        return save();
    }

    static boolean isConfigLayoutDebugEnabled() {
        return configLayoutDebugEnabled;
    }

    static boolean setConfigLayoutDebugEnabled(boolean enabled) {
        configLayoutDebugEnabled = enabled;
        return save();
    }

    static boolean isConfigLayoutAspectLocked() {
        return configLayoutAspectLocked;
    }

    static boolean setConfigLayoutAspectLocked(boolean locked) {
        configLayoutAspectLocked = locked;
        return save();
    }

    static boolean isConfigLayoutGridEnabled() {
        return configLayoutGridEnabled;
    }

    static boolean setConfigLayoutGridEnabled(boolean enabled) {
        configLayoutGridEnabled = enabled;
        return save();
    }

    static boolean isConfigLayoutSnapEnabled() {
        return configLayoutSnapEnabled;
    }

    static boolean setConfigLayoutSnapEnabled(boolean enabled) {
        configLayoutSnapEnabled = enabled;
        return save();
    }

    static boolean hasCustomConfigLayout() {
        return configLayoutCustom;
    }

    static double[] getConfigLayout() {
        return new double[]{configLayoutX, configLayoutY, configLayoutWidth, configLayoutHeight};
    }

    static int getConfigLayoutBaseWidth() {
        return configLayoutBaseWidth;
    }

    static int getConfigLayoutBaseHeight() {
        return configLayoutBaseHeight;
    }

    static boolean setConfigLayout(double x, double y, double width, double height) {
        return setConfigLayout(x, y, width, height, configLayoutBaseWidth, configLayoutBaseHeight);
    }

    static boolean setConfigLayout(double x, double y, double width, double height, int baseWidth, int baseHeight) {
        configLayoutCustom = true;
        configLayoutX = clamp(x, 0.0D, 1.0D);
        configLayoutY = clamp(y, 0.0D, 1.0D);
        configLayoutWidth = clamp(width, 0.0D, 1.0D);
        configLayoutHeight = clamp(height, 0.0D, 1.0D);
        configLayoutBaseWidth = Math.max(1, baseWidth);
        configLayoutBaseHeight = Math.max(1, baseHeight);
        return save();
    }

    static boolean resetConfigLayout() {
        Properties defaults = createDefaultProperties();
        configLayoutCustom = Boolean.parseBoolean(defaults.getProperty(
                CONFIG_LAYOUT_CUSTOM_KEY,
                Boolean.toString(configLayoutCustom)
        ));
        configLayoutX = readUnitDouble(defaults, CONFIG_LAYOUT_X_KEY, configLayoutX);
        configLayoutY = readUnitDouble(defaults, CONFIG_LAYOUT_Y_KEY, configLayoutY);
        configLayoutWidth = readUnitDouble(defaults, CONFIG_LAYOUT_WIDTH_KEY, configLayoutWidth);
        configLayoutHeight = readUnitDouble(defaults, CONFIG_LAYOUT_HEIGHT_KEY, configLayoutHeight);
        configLayoutBaseWidth = readPositiveInt(defaults, CONFIG_LAYOUT_BASE_WIDTH_KEY, configLayoutBaseWidth);
        configLayoutBaseHeight = readPositiveInt(defaults, CONFIG_LAYOUT_BASE_HEIGHT_KEY, configLayoutBaseHeight);
        return save();
    }

    static boolean hasConfigWidgetLayout(String id) {
        return configWidgetLayouts.containsKey(id);
    }

    static double[] getConfigWidgetLayout(String id) {
        double[] values = configWidgetLayouts.get(id);
        return values == null ? new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D} : values.clone();
    }

    static boolean setConfigWidgetLayout(String id, double x, double y, double width, double height) {
        return setConfigWidgetLayout(id, x, y, width, height, 0, 0);
    }

    static boolean setConfigWidgetLayout(String id, double x, double y, double width, double height, int baseWidth, int baseHeight) {
        if (!isSafeId(id)) {
            return false;
        }
        configWidgetLayouts.put(id, new double[]{
                clamp(x, -2.0D, 3.0D),
                clamp(y, -2.0D, 3.0D),
                clamp(width, 0.01D, 3.0D),
                clamp(height, 0.01D, 3.0D),
                Math.max(0, baseWidth),
                Math.max(0, baseHeight)
        });
        return save();
    }
    static boolean resetConfigWidgetLayout(String id) {
        if (!isSafeId(id)) {
            return false;
        }
        Properties defaults = createDefaultProperties();
        String prefix = CONFIG_WIDGET_PREFIX + id;
        double x = readDouble(defaults, prefix + ".x", 0.0D);
        double y = readDouble(defaults, prefix + ".y", 0.0D);
        double width = readDouble(defaults, prefix + ".width", 0.0D);
        double height = readDouble(defaults, prefix + ".height", 0.0D);
        int baseWidth = readPositiveInt(defaults, prefix + ".baseWidth", 0);
        int baseHeight = readPositiveInt(defaults, prefix + ".baseHeight", 0);
        if (width > 0.0D && height > 0.0D) {
            configWidgetLayouts.put(id, new double[]{
                    clamp(x, -2.0D, 3.0D),
                    clamp(y, -2.0D, 3.0D),
                    clamp(width, 0.01D, 3.0D),
                    clamp(height, 0.01D, 3.0D),
                    baseWidth,
                    baseHeight
            });
        } else {
            configWidgetLayouts.remove(id);
        }
        return save();
    }

    static boolean resetConfigWidgetLayouts() {
        readWidgetLayouts(createDefaultProperties());
        return save();
    }

    static String getConfigText(String id, String fallback) {
        String value = configTexts.get(id);
        return value == null ? fallback : value;
    }

    static boolean setConfigText(String id, String text) {
        if (!isSafeId(id)) {
            return false;
        }
        if (text == null || text.isEmpty()) {
            configTexts.remove(id);
        } else {
            configTexts.put(id, text);
        }
        return save();
    }

    static boolean resetConfigText(String id) {
        if (!isSafeId(id)) {
            return false;
        }
        String value = createDefaultProperties().getProperty(CONFIG_TEXT_PREFIX + id);
        if (value == null || value.isEmpty()) {
            configTexts.remove(id);
        } else {
            configTexts.put(id, value);
        }
        return save();
    }

    private static void applyConfigProperties(Properties properties) {
        effectEnabled = Boolean.parseBoolean(properties.getProperty(EFFECT_ENABLED_KEY, Boolean.toString(effectEnabled)));
        playerFreezeEnabled = Boolean.parseBoolean(properties.getProperty(
                PLAYER_FREEZE_ENABLED_KEY,
                Boolean.toString(playerFreezeEnabled)
        ));
        crossDimensionTravelEnabled = Boolean.parseBoolean(properties.getProperty(
                CROSS_DIMENSION_TRAVEL_ENABLED_KEY,
                Boolean.toString(crossDimensionTravelEnabled)
        ));
        zoomHeightsLinked = Boolean.parseBoolean(properties.getProperty(
                ZOOM_HEIGHTS_LINKED_KEY,
                Boolean.toString(zoomHeightsLinked)
        ));
        zoomOutStageHeights = readStageHeights(properties, ZOOM_OUT_STAGE_KEY_PREFIX, DEFAULT_STAGE_HEIGHTS);
        zoomInStageHeights = readStageHeights(properties, ZOOM_IN_STAGE_KEY_PREFIX, DEFAULT_STAGE_HEIGHTS);
        if (zoomHeightsLinked) {
            zoomInStageHeights = zoomOutStageHeights.clone();
        }
        netherZoomHeightsLinked = Boolean.parseBoolean(properties.getProperty(
                NETHER_ZOOM_HEIGHTS_LINKED_KEY,
                Boolean.toString(zoomHeightsLinked)
        ));
        netherZoomOutStageHeights = readStageHeights(properties, NETHER_ZOOM_OUT_STAGE_KEY_PREFIX, zoomOutStageHeights);
        netherZoomInStageHeights = readStageHeights(properties, NETHER_ZOOM_IN_STAGE_KEY_PREFIX, zoomInStageHeights);
        if (netherZoomHeightsLinked) {
            netherZoomInStageHeights = netherZoomOutStageHeights.clone();
        }
        endZoomHeightsLinked = Boolean.parseBoolean(properties.getProperty(
                END_ZOOM_HEIGHTS_LINKED_KEY,
                Boolean.toString(zoomHeightsLinked)
        ));
        endZoomOutStageHeights = readStageHeights(properties, END_ZOOM_OUT_STAGE_KEY_PREFIX, zoomOutStageHeights);
        endZoomInStageHeights = readStageHeights(properties, END_ZOOM_IN_STAGE_KEY_PREFIX, zoomInStageHeights);
        if (endZoomHeightsLinked) {
            endZoomInStageHeights = endZoomOutStageHeights.clone();
        }
        zoomOutStageTicks = readStageTicks(properties, ZOOM_OUT_STAGE_TICKS_KEY_PREFIX, DEFAULT_STAGE_TICKS);
        zoomInStageTicks = readStageTicks(properties, ZOOM_IN_STAGE_TICKS_KEY_PREFIX, DEFAULT_STAGE_TICKS);
        zoomStageGlideHeight = readClampedDouble(properties, ZOOM_STAGE_GLIDE_HEIGHT_KEY, zoomStageGlideHeight, MIN_ZOOM_STAGE_GLIDE_HEIGHT, MAX_ZOOM_STAGE_GLIDE_HEIGHT);
        zoomStageGlideTicks = readClampedInt(properties, ZOOM_STAGE_GLIDE_TICKS_KEY, zoomStageGlideTicks, MIN_STAGE_TICKS, MAX_STAGE_TICKS);
        bodyCameraHeight = readClampedDouble(properties, BODY_CAMERA_HEIGHT_KEY, bodyCameraHeight, MIN_BODY_CAMERA_HEIGHT, MAX_BODY_CAMERA_HEIGHT);
        bodyGlideHeight = readClampedDouble(properties, BODY_GLIDE_HEIGHT_KEY, bodyGlideHeight, MIN_BODY_GLIDE_HEIGHT, MAX_BODY_GLIDE_HEIGHT);
        bodyGlideTicks = readClampedInt(properties, BODY_GLIDE_TICKS_KEY, bodyGlideTicks, MIN_STAGE_TICKS, MAX_STAGE_TICKS);
        localPlayerHideTicks = readClampedInt(properties, LOCAL_PLAYER_HIDE_TICKS_KEY, localPlayerHideTicks, MIN_LOCAL_PLAYER_HIDE_TICKS, MAX_LOCAL_PLAYER_HIDE_TICKS);
        customSoundsEnabled = Boolean.parseBoolean(properties.getProperty(
                CUSTOM_SOUNDS_ENABLED_KEY,
                Boolean.toString(customSoundsEnabled)
        ));
        minecraftSoundVolume = readClampedDouble(properties, MINECRAFT_SOUND_VOLUME_KEY, minecraftSoundVolume, MIN_SOUND_VOLUME, MAX_SOUND_VOLUME);
        customSoundVolume = readClampedDouble(properties, CUSTOM_SOUND_VOLUME_KEY, customSoundVolume, MIN_SOUND_VOLUME, MAX_SOUND_VOLUME);
        warpPlateTransitionsEnabled = Boolean.parseBoolean(properties.getProperty(
                WARP_PLATE_TRANSITIONS_ENABLED_KEY,
                Boolean.toString(warpPlateTransitionsEnabled)
        ));
        externalTeleportTransitionsEnabled = Boolean.parseBoolean(properties.getProperty(
                EXTERNAL_TELEPORT_TRANSITIONS_ENABLED_KEY,
                Boolean.toString(externalTeleportTransitionsEnabled)
        ));
        fallbackChunkFadeEnabled = Boolean.parseBoolean(properties.getProperty(
                FALLBACK_CHUNK_FADE_ENABLED_KEY,
                Boolean.toString(fallbackChunkFadeEnabled)
        ));
        configLayoutEditorButtonVisible = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_EDITOR_BUTTON_VISIBLE_KEY,
                Boolean.toString(configLayoutEditorButtonVisible)
        ));
        configLayoutDebugEnabled = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_DEBUG_ENABLED_KEY,
                Boolean.toString(configLayoutDebugEnabled)
        ));
        configLayoutAspectLocked = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_ASPECT_LOCKED_KEY,
                Boolean.toString(configLayoutAspectLocked)
        ));
        configLayoutGridEnabled = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_GRID_ENABLED_KEY,
                Boolean.toString(configLayoutGridEnabled)
        ));
        configLayoutSnapEnabled = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_SNAP_ENABLED_KEY,
                Boolean.toString(configLayoutSnapEnabled)
        ));
        configLayoutCustom = Boolean.parseBoolean(properties.getProperty(
                CONFIG_LAYOUT_CUSTOM_KEY,
                Boolean.toString(configLayoutCustom)
        ));
        configLayoutX = readUnitDouble(properties, CONFIG_LAYOUT_X_KEY, configLayoutX);
        configLayoutY = readUnitDouble(properties, CONFIG_LAYOUT_Y_KEY, configLayoutY);
        configLayoutWidth = readUnitDouble(properties, CONFIG_LAYOUT_WIDTH_KEY, configLayoutWidth);
        configLayoutHeight = readUnitDouble(properties, CONFIG_LAYOUT_HEIGHT_KEY, configLayoutHeight);
        configLayoutBaseWidth = readPositiveInt(properties, CONFIG_LAYOUT_BASE_WIDTH_KEY, configLayoutBaseWidth);
        configLayoutBaseHeight = readPositiveInt(properties, CONFIG_LAYOUT_BASE_HEIGHT_KEY, configLayoutBaseHeight);
        readWidgetLayouts(properties);
        readConfigTexts(properties);
    }

    private static void resetToDefaults() {
        applyConfigProperties(createDefaultProperties());
    }

    private static Properties createDefaultProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(DEFAULT_CONFIG_PROPERTIES));
        } catch (IOException ignored) {
        }
        return properties;
    }

    private static double[] readStageHeights(Properties properties, String prefix, double[] defaults) {
        double[] values = defaults.clone();
        for (int i = 0; i < values.length; i++) {
            values[i] = readDouble(properties, prefix + (i + 1), values[i]);
        }
        return sanitizeStageHeights(values);
    }

    private static int[] readStageTicks(Properties properties, String prefix, int[] defaults) {
        int[] values = defaults.clone();
        for (int i = 0; i < values.length; i++) {
            values[i] = readClampedInt(properties, prefix + (i + 1), values[i], MIN_STAGE_TICKS, MAX_STAGE_TICKS);
        }
        return sanitizeStageTicks(values);
    }

    private static void readWidgetLayouts(Properties properties) {
        configWidgetLayouts.clear();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(CONFIG_WIDGET_PREFIX) || !key.endsWith(".x")) {
                continue;
            }
            String id = key.substring(CONFIG_WIDGET_PREFIX.length(), key.length() - 2);
            if (!isSafeId(id)) {
                continue;
            }
            String prefix = CONFIG_WIDGET_PREFIX + id;
            double x = readDouble(properties, prefix + ".x", 0.0D);
            double y = readDouble(properties, prefix + ".y", 0.0D);
            double width = readDouble(properties, prefix + ".width", 0.0D);
            double height = readDouble(properties, prefix + ".height", 0.0D);
            int baseWidth = readPositiveInt(properties, prefix + ".baseWidth", 0);
            int baseHeight = readPositiveInt(properties, prefix + ".baseHeight", 0);
            if (width > 0.0D && height > 0.0D) {
                configWidgetLayouts.put(id, new double[]{
                        clamp(x, -2.0D, 3.0D),
                        clamp(y, -2.0D, 3.0D),
                        clamp(width, 0.01D, 3.0D),
                        clamp(height, 0.01D, 3.0D),
                        baseWidth,
                        baseHeight
                });
            }
        }
    }
    private static void readConfigTexts(Properties properties) {
        configTexts.clear();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(CONFIG_TEXT_PREFIX)) {
                continue;
            }
            String id = key.substring(CONFIG_TEXT_PREFIX.length());
            if (isSafeId(id)) {
                configTexts.put(id, properties.getProperty(key, ""));
            }
        }
    }
    private static boolean prepareLoadedProperties(Properties properties) {
        if (!isLegacyCompactLayoutConfig(properties)) {
            return false;
        }

        restoreDefaultLayoutProperties(properties);
        return true;
    }

    private static boolean isLegacyCompactLayoutConfig(Properties properties) {
        return !hasPropertyWithPrefix(properties, CONFIG_WIDGET_PREFIX)
                && !hasPropertyWithPrefix(properties, CONFIG_TEXT_PREFIX)
                && (properties.containsKey(CONFIG_LAYOUT_CUSTOM_KEY)
                || properties.containsKey(CONFIG_LAYOUT_BASE_WIDTH_KEY)
                || properties.containsKey(CONFIG_LAYOUT_BASE_HEIGHT_KEY)
                || properties.containsKey(CONFIG_LAYOUT_WIDTH_KEY)
                || properties.containsKey(CONFIG_LAYOUT_HEIGHT_KEY));
    }

    private static boolean hasPropertyWithPrefix(Properties properties, String prefix) {
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static void restoreDefaultLayoutProperties(Properties properties) {
        for (String key : properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("configLayout")
                        || key.startsWith(CONFIG_WIDGET_PREFIX)
                        || key.startsWith(CONFIG_TEXT_PREFIX))
                .toList()) {
            properties.remove(key);
        }

        Properties defaults = createDefaultProperties();
        for (String key : defaults.stringPropertyNames()) {
            if (key.startsWith("configLayout")
                    || key.startsWith(CONFIG_WIDGET_PREFIX)
                    || key.startsWith(CONFIG_TEXT_PREFIX)) {
                properties.setProperty(key, defaults.getProperty(key));
            }
        }
    }

    private static double readDouble(Properties properties, String key, double fallback) {
        try {
            return Double.parseDouble(properties.getProperty(key, Double.toString(fallback)));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double readUnitDouble(Properties properties, String key, double fallback) {
        return clamp(readDouble(properties, key, fallback), 0.0D, 1.0D);
    }

    private static double readClampedDouble(Properties properties, String key, double fallback, double min, double max) {
        return clamp(readDouble(properties, key, fallback), min, max);
    }

    private static int readClampedInt(Properties properties, String key, int fallback, int min, int max) {
        try {
            return clamp(Integer.parseInt(properties.getProperty(key, Integer.toString(fallback))), min, max);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int readPositiveInt(Properties properties, String key, int fallback) {
        try {
            return Math.max(0, Integer.parseInt(properties.getProperty(key, Integer.toString(fallback))));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double roundStageHeight(double value) {
        return Math.rint(value);
    }

    private static boolean isSafeId(String id) {
        return id != null && id.matches("[a-z0-9_]+") && id.length() <= 64;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean save() {
        if (configPath == null) {
            configPath = resolveConfigPath();
        }

        Properties properties = new Properties();
        properties.setProperty(EFFECT_ENABLED_KEY, Boolean.toString(effectEnabled));
        properties.setProperty(PLAYER_FREEZE_ENABLED_KEY, Boolean.toString(playerFreezeEnabled));
        properties.setProperty(CROSS_DIMENSION_TRAVEL_ENABLED_KEY, Boolean.toString(crossDimensionTravelEnabled));
        properties.setProperty(ZOOM_HEIGHTS_LINKED_KEY, Boolean.toString(zoomHeightsLinked));
        writeStageHeights(properties, ZOOM_OUT_STAGE_KEY_PREFIX, zoomOutStageHeights);
        writeStageHeights(properties, ZOOM_IN_STAGE_KEY_PREFIX, zoomInStageHeights);
        properties.setProperty(NETHER_ZOOM_HEIGHTS_LINKED_KEY, Boolean.toString(netherZoomHeightsLinked));
        writeStageHeights(properties, NETHER_ZOOM_OUT_STAGE_KEY_PREFIX, netherZoomOutStageHeights);
        writeStageHeights(properties, NETHER_ZOOM_IN_STAGE_KEY_PREFIX, netherZoomInStageHeights);
        properties.setProperty(END_ZOOM_HEIGHTS_LINKED_KEY, Boolean.toString(endZoomHeightsLinked));
        writeStageHeights(properties, END_ZOOM_OUT_STAGE_KEY_PREFIX, endZoomOutStageHeights);
        writeStageHeights(properties, END_ZOOM_IN_STAGE_KEY_PREFIX, endZoomInStageHeights);
        writeStageTicks(properties, ZOOM_OUT_STAGE_TICKS_KEY_PREFIX, zoomOutStageTicks);
        writeStageTicks(properties, ZOOM_IN_STAGE_TICKS_KEY_PREFIX, zoomInStageTicks);
        properties.setProperty(ZOOM_STAGE_GLIDE_HEIGHT_KEY, Double.toString(zoomStageGlideHeight));
        properties.setProperty(ZOOM_STAGE_GLIDE_TICKS_KEY, Integer.toString(zoomStageGlideTicks));
        properties.setProperty(BODY_CAMERA_HEIGHT_KEY, Double.toString(bodyCameraHeight));
        properties.setProperty(BODY_GLIDE_HEIGHT_KEY, Double.toString(bodyGlideHeight));
        properties.setProperty(BODY_GLIDE_TICKS_KEY, Integer.toString(bodyGlideTicks));
        properties.setProperty(LOCAL_PLAYER_HIDE_TICKS_KEY, Integer.toString(localPlayerHideTicks));
        properties.setProperty(CUSTOM_SOUNDS_ENABLED_KEY, Boolean.toString(customSoundsEnabled));
        properties.setProperty(MINECRAFT_SOUND_VOLUME_KEY, Double.toString(minecraftSoundVolume));
        properties.setProperty(CUSTOM_SOUND_VOLUME_KEY, Double.toString(customSoundVolume));
        properties.setProperty(WARP_PLATE_TRANSITIONS_ENABLED_KEY, Boolean.toString(warpPlateTransitionsEnabled));
        properties.setProperty(EXTERNAL_TELEPORT_TRANSITIONS_ENABLED_KEY, Boolean.toString(externalTeleportTransitionsEnabled));
        properties.setProperty(FALLBACK_CHUNK_FADE_ENABLED_KEY, Boolean.toString(fallbackChunkFadeEnabled));
        properties.setProperty(CONFIG_LAYOUT_EDITOR_BUTTON_VISIBLE_KEY, Boolean.toString(configLayoutEditorButtonVisible));
        properties.setProperty(CONFIG_LAYOUT_DEBUG_ENABLED_KEY, Boolean.toString(configLayoutDebugEnabled));
        properties.setProperty(CONFIG_LAYOUT_ASPECT_LOCKED_KEY, Boolean.toString(configLayoutAspectLocked));
        properties.setProperty(CONFIG_LAYOUT_GRID_ENABLED_KEY, Boolean.toString(configLayoutGridEnabled));
        properties.setProperty(CONFIG_LAYOUT_SNAP_ENABLED_KEY, Boolean.toString(configLayoutSnapEnabled));
        properties.setProperty(CONFIG_LAYOUT_CUSTOM_KEY, Boolean.toString(configLayoutCustom));
        properties.setProperty(CONFIG_LAYOUT_X_KEY, Double.toString(configLayoutX));
        properties.setProperty(CONFIG_LAYOUT_Y_KEY, Double.toString(configLayoutY));
        properties.setProperty(CONFIG_LAYOUT_WIDTH_KEY, Double.toString(configLayoutWidth));
        properties.setProperty(CONFIG_LAYOUT_HEIGHT_KEY, Double.toString(configLayoutHeight));
        properties.setProperty(CONFIG_LAYOUT_BASE_WIDTH_KEY, Integer.toString(configLayoutBaseWidth));
        properties.setProperty(CONFIG_LAYOUT_BASE_HEIGHT_KEY, Integer.toString(configLayoutBaseHeight));
        for (Map.Entry<String, double[]> entry : configWidgetLayouts.entrySet()) {
            double[] values = entry.getValue();
            String prefix = CONFIG_WIDGET_PREFIX + entry.getKey();
            properties.setProperty(prefix + ".x", Double.toString(values[0]));
            properties.setProperty(prefix + ".y", Double.toString(values[1]));
            properties.setProperty(prefix + ".width", Double.toString(values[2]));
            properties.setProperty(prefix + ".height", Double.toString(values[3]));
            if (values.length > 5) {
                properties.setProperty(prefix + ".baseWidth", Integer.toString((int) Math.round(values[4])));
                properties.setProperty(prefix + ".baseHeight", Integer.toString((int) Math.round(values[5])));
            }
        }
        for (Map.Entry<String, String> entry : configTexts.entrySet()) {
            properties.setProperty(CONFIG_TEXT_PREFIX + entry.getKey(), entry.getValue());
        }

        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "Grand Teleport client settings");
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
    private static Path resolveConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static Path resolveLegacyConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(LEGACY_FILE_NAME);
    }

    private static void migrateLegacyConfig() {
        Path legacyPath = resolveLegacyConfigPath();
        if (Files.exists(configPath) || !Files.exists(legacyPath)) {
            return;
        }
        try {
            Files.createDirectories(configPath.getParent());
            Files.copy(legacyPath, configPath);
        } catch (IOException ignored) {
            configPath = legacyPath;
        }
    }

    private static ZoomDimension sanitizeZoomDimension(ZoomDimension dimension) {
        return dimension == null ? ZoomDimension.OVERWORLD : dimension;
    }

    enum ZoomDimension {
        OVERWORLD,
        NETHER,
        END;

        static ZoomDimension fromLevel(ResourceKey<Level> dimension) {
            if (Level.NETHER.equals(dimension)) {
                return NETHER;
            }
            if (Level.END.equals(dimension)) {
                return END;
            }
            return OVERWORLD;
        }
    }

    private static void writeStageHeights(Properties properties, String prefix, double[] values) {
        double[] sanitized = sanitizeStageHeights(values);
        for (int i = 0; i < sanitized.length; i++) {
            properties.setProperty(prefix + (i + 1), Integer.toString((int) sanitized[i]));
        }
    }

    private static void writeStageTicks(Properties properties, String prefix, int[] values) {
        int[] sanitized = sanitizeStageTicks(values);
        for (int i = 0; i < sanitized.length; i++) {
            properties.setProperty(prefix + (i + 1), Integer.toString(sanitized[i]));
        }
    }
}
