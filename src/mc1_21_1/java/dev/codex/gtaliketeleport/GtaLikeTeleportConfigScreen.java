package dev.codex.gtaliketeleport;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class GtaLikeTeleportConfigScreen extends Screen {
    private static final int PANEL_COLOR = 0x66000000;
    private static final int PANEL_BORDER_COLOR = 0x77FFFFFF;
    private static final int DEBUG_BORDER_COLOR = 0xFFFFFF55;
    private static final int DEBUG_HANDLE_COLOR = 0xCCFFFF55;
    private static final int DEBUG_ITEM_BORDER_COLOR = 0x88FFFF55;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int MUTED_TEXT_COLOR = 0xFFA0A0A0;
    private static final int OUTER_PADDING_LEFT = 14;
    private static final int OUTER_PADDING_TOP = 14;
    private static final int OUTER_PADDING_RIGHT = 14;
    private static final int OUTER_PADDING_BOTTOM = 0;
    private static final int MIN_PANEL_WIDTH = 300;
    private static final int MIN_PANEL_HEIGHT = 142;
    private static final int MAX_PANEL_MARGIN = 28;
    private static final int DEBUG_TOOLBAR_MARGIN = 6;
    private static final int DEBUG_TOOLBAR_BUTTON_HEIGHT = 20;
    private static final int DEBUG_TOOLBAR_GAP = 6;
    private static final int DEBUG_TOOLBAR_ROW_STEP = 22;
    private static final int DEBUG_PANEL_TOP_MARGIN = 84;
    private static final double MAX_CONTENT_WIDTH_RATIO = 0.72D;
    private static final int RESIZE_HIT_SIZE = 10;
    private static final int SNAP_DISTANCE = 5;
    private static final int GRID_SIZE = 8;
    private static final int DEBUG_GRID_COLOR = 0x22FFFFFF;
    private static final int DEBUG_CENTER_GUIDE_COLOR = 0x77AACCFF;
    private static final int DEBUG_PANEL_GUIDE_COLOR = 0x66FFFFFF;
    private static final int DEBUG_SELECTED_GUIDE_COLOR = 0x88FFFF55;
    private static final double DEFAULT_PANEL_WIDTH_RATIO = 0.58D;
    private static final double DEFAULT_LINKED_PANEL_HEIGHT_RATIO = 0.24D;
    private static final double DEFAULT_UNLINKED_PANEL_HEIGHT_RATIO = 0.31D;
    private static final ResourceLocation OVERWORLD_DIMENSION_ICON = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/grass_block_side.png");
    private static final ResourceLocation NETHER_DIMENSION_ICON = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/netherrack.png");
    private static final ResourceLocation END_DIMENSION_ICON = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/end_stone.png");

    private static final String ITEM_PANEL = "panel";
    private static final String ITEM_TITLE = "title";
    private static final String ITEM_DESCRIPTION = "description";
    private static final String ITEM_STATUS_LINKED = "status_linked";
    private static final String ITEM_STATUS_UNLINKED = "status_unlinked";
    private static final String ITEM_LINKED_SLIDER = "linked_slider";
    private static final String ITEM_ZOOM_OUT_SLIDER = "zoom_out_slider";
    private static final String ITEM_ZOOM_IN_SLIDER = "zoom_in_slider";
    private static final String ITEM_LINK_BUTTON = "link_button";
    private static final String ITEM_DIMENSION_OVERWORLD = "dimension_overworld";
    private static final String ITEM_DIMENSION_NETHER = "dimension_nether";
    private static final String ITEM_DIMENSION_END = "dimension_end";
    private static final String ITEM_RESET_BUTTON = "reset_button";
    private static final String ITEM_DONE_BUTTON = "done_button";
    private static final String ITEM_PREV_PAGE_BUTTON = "prev_page_button";
    private static final String ITEM_NEXT_PAGE_BUTTON = "next_page_button";
    private static final String ITEM_TAB_GENERAL = "tab_general";
    private static final String ITEM_TAB_ZOOM_STAGE = "tab_zoom_stage";
    private static final String ITEM_TAB_ZOOM_STAGE_2 = "tab_zoom_stage_2";
    private static final String ITEM_TAB_SOUNDS = "tab_sounds";
    private static final String ITEM_TAB_OTHERS = "tab_others";
    private static final String ITEM_GENERAL_TITLE = "general_title";
    private static final String ITEM_GENERAL_DESCRIPTION = "general_description";
    private static final String ITEM_EFFECT_LABEL = "effect_label";
    private static final String ITEM_EFFECT_TOGGLE = "effect_toggle";
    private static final String ITEM_MOVEMENT_LABEL = "movement_label";
    private static final String ITEM_MOVEMENT_TOGGLE = "movement_toggle";
    private static final String ITEM_CROSS_DIMENSION_TRAVEL_LABEL = "cross_dimension_travel_label";
    private static final String ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE = "cross_dimension_travel_toggle";
    private static final String ITEM_ADVANCED1_TITLE = "advanced1_title";
    private static final String ITEM_ADVANCED1_DESCRIPTION = "advanced1_description";
    private static final String ITEM_ADVANCED2_TITLE = "advanced2_title";
    private static final String ITEM_ADVANCED2_DESCRIPTION = "advanced2_description";
    private static final String ITEM_ADVANCED3_TITLE = "advanced3_title";
    private static final String ITEM_ADVANCED3_DESCRIPTION = "advanced3_description";
    private static final String ITEM_ZOOM_STAGE_GLIDE_SLIDER = "zoom_stage_glide_slider";
    private static final String ITEM_ZOOM_STAGE_GLIDE_TICKS_LABEL = "zoom_stage_glide_ticks_label";
    private static final String ITEM_ZOOM_STAGE_GLIDE_TICKS_FIELD = "zoom_stage_glide_ticks_field";
    private static final String ITEM_ZOOM_OUT_TICKS_LABEL = "zoom_out_ticks_label";
    private static final String ITEM_ZOOM_OUT_TICKS_FIELD = "zoom_out_ticks_field";
    private static final String ITEM_ZOOM_IN_TICKS_LABEL = "zoom_in_ticks_label";
    private static final String ITEM_ZOOM_IN_TICKS_FIELD = "zoom_in_ticks_field";
    private static final String ITEM_BODY_HEIGHT_SLIDER = "body_height_slider";
    private static final String ITEM_BODY_GLIDE_SLIDER = "body_glide_slider";
    private static final String ITEM_BODY_GLIDE_TICKS_LABEL = "body_glide_ticks_label";
    private static final String ITEM_BODY_GLIDE_TICKS_FIELD = "body_glide_ticks_field";
    private static final String ITEM_PLAYER_HIDE_LABEL = "player_hide_label";
    private static final String ITEM_PLAYER_HIDE_TICKS_FIELD = "player_hide_ticks_field";
    private static final String ITEM_SOUNDS_TITLE = "sounds_title";
    private static final String ITEM_SOUNDS_DESCRIPTION = "sounds_description";
    private static final String ITEM_SOUND_MODE_LABEL = "sound_mode_label";
    private static final String ITEM_SOUND_MODE_TOGGLE = "sound_mode_toggle";
    private static final String ITEM_MINECRAFT_VOLUME_SLIDER = "minecraft_volume_slider";
    private static final String ITEM_CUSTOM_VOLUME_SLIDER = "custom_volume_slider";
    private static final String ITEM_OTHERS_TITLE = "others_title";
    private static final String ITEM_OTHERS_DESCRIPTION = "others_description";
    private static final String ITEM_WARP_PLATE_LABEL = "warp_plate_label";
    private static final String ITEM_WARP_PLATE_TOGGLE = "warp_plate_toggle";
    private static final String ITEM_EXTERNAL_TELEPORT_LABEL = "external_teleport_label";
    private static final String ITEM_EXTERNAL_TELEPORT_TOGGLE = "external_teleport_toggle";
    private static final String ITEM_FALLBACK_CHUNK_FADE_LABEL = "fallback_chunk_fade_label";
    private static final String ITEM_FALLBACK_CHUNK_FADE_TOGGLE = "fallback_chunk_fade_toggle";

    private final Screen parent;
    private ConfigPage currentPage = ConfigPage.GENERAL;
    private boolean linked;
    private boolean effectEnabled;
    private boolean movementAllowed;
    private boolean crossDimensionTravelEnabled;
    private boolean layoutDebugEnabled;
    private boolean layoutAspectLocked;
    private boolean layoutGridEnabled;
    private boolean layoutSnapEnabled;
    private boolean updatingValueWidgets;
    private double[] zoomOutHeights;
    private double[] zoomInHeights;
    private GtaLikeTeleportConfig.ZoomDimension selectedZoomDimension;
    private int[] zoomOutStageTicks;
    private int[] zoomInStageTicks;
    private double zoomStageGlideHeight;
    private int zoomStageGlideTicks;
    private double bodyCameraHeight;
    private double bodyGlideHeight;
    private int bodyGlideTicks;
    private int localPlayerHideTicks;
    private boolean customSoundsEnabled;
    private double minecraftSoundVolume;
    private double customSoundVolume;
    private boolean warpPlateTransitionsEnabled;
    private boolean externalTeleportTransitionsEnabled;
    private boolean fallbackChunkFadeEnabled;
    private StageHeightSlider linkedSlider;
    private StageHeightSlider zoomOutSlider;
    private StageHeightSlider zoomInSlider;
    private SingleValueSlider zoomStageGlideSlider;
    private SingleValueSlider bodyHeightSlider;
    private SingleValueSlider bodyGlideSlider;
    private SingleValueSlider minecraftSoundVolumeSlider;
    private SingleValueSlider customSoundVolumeSlider;
    private EditBox zoomStageGlideTicksEditBox;
    private EditBox zoomOutTicksEditBox;
    private EditBox zoomInTicksEditBox;
    private EditBox bodyGlideTicksEditBox;
    private EditBox playerHideTicksEditBox;
    private LinkLockButton linkButton;
    private DimensionIconButton overworldDimensionButton;
    private DimensionIconButton netherDimensionButton;
    private DimensionIconButton endDimensionButton;
    private Button resetButton;
    private Button doneButton;
    private Button prevPageButton;
    private Button nextPageButton;
    private Button[] pageTabButtons = new Button[0];
    private Button effectToggleButton;
    private Button movementToggleButton;
    private Button crossDimensionTravelToggleButton;
    private Button soundModeToggleButton;
    private Button warpPlateToggleButton;
    private Button externalTeleportToggleButton;
    private Button fallbackChunkFadeToggleButton;
    private Button layoutDebugButton;
    private Button aspectButton;
    private Button resetLayoutButton;
    private Button resetItemSizeButton;
    private Button gridButton;
    private Button snapButton;
    private EditBox textEditBox;
    private EditBox layoutValueEditBox;
    private Button applyLayoutValueButton;
    private boolean textEditorVisible;
    private boolean layoutValueEditorVisible;
    private boolean updatingTextEditor;
    private boolean updatingLayoutValueEditor;
    private String selectedLayoutItem = ITEM_PANEL;
    private String editingLayoutItem;
    private LayoutEditAction layoutEditAction = LayoutEditAction.NONE;
    private LayoutRect editStartRect;
    private LayoutRect editingRect;
    private double editStartMouseX;
    private double editStartMouseY;
    private int sessionLayoutBaseWidth;
    private int sessionLayoutBaseHeight;

    GtaLikeTeleportConfigScreen(Screen parent) {
        super(Component.literal("Grand Teleport Settings"));
        this.parent = parent;
        this.selectedZoomDimension = getInitialZoomDimension();
        this.linked = GtaLikeTeleportConfig.areZoomHeightsLinked(this.selectedZoomDimension);
        this.effectEnabled = GtaLikeTeleportConfig.isEffectEnabled();
        this.movementAllowed = !GtaLikeTeleportConfig.isPlayerFreezeEnabled();
        this.crossDimensionTravelEnabled = GtaLikeTeleportConfig.isCrossDimensionTravelEnabled();
        this.layoutDebugEnabled = GtaLikeTeleportConfig.isConfigLayoutEditorButtonVisible() && GtaLikeTeleportConfig.isConfigLayoutDebugEnabled();
        this.layoutAspectLocked = GtaLikeTeleportConfig.isConfigLayoutAspectLocked();
        this.layoutGridEnabled = GtaLikeTeleportConfig.isConfigLayoutGridEnabled();
        this.layoutSnapEnabled = GtaLikeTeleportConfig.isConfigLayoutSnapEnabled();
        this.zoomOutHeights = GtaLikeTeleportConfig.getZoomOutStageHeights(this.selectedZoomDimension);
        this.zoomInHeights = GtaLikeTeleportConfig.getRawZoomInStageHeights(this.selectedZoomDimension);
        this.zoomOutStageTicks = GtaLikeTeleportConfig.getZoomOutStageTicks();
        this.zoomInStageTicks = GtaLikeTeleportConfig.getZoomInStageTicks();
        this.zoomStageGlideHeight = GtaLikeTeleportConfig.getZoomStageGlideHeight();
        this.zoomStageGlideTicks = GtaLikeTeleportConfig.getZoomStageGlideTicks();
        this.bodyCameraHeight = GtaLikeTeleportConfig.getBodyCameraHeight();
        this.bodyGlideHeight = GtaLikeTeleportConfig.getBodyGlideHeight();
        this.bodyGlideTicks = GtaLikeTeleportConfig.getBodyGlideTicks();
        this.localPlayerHideTicks = GtaLikeTeleportConfig.getLocalPlayerHideTicks();
        this.customSoundsEnabled = GtaLikeTeleportConfig.isCustomSoundsEnabled();
        this.minecraftSoundVolume = GtaLikeTeleportConfig.getMinecraftSoundVolume();
        this.customSoundVolume = GtaLikeTeleportConfig.getCustomSoundVolume();
        this.warpPlateTransitionsEnabled = GtaLikeTeleportConfig.isWarpPlateTransitionsEnabled();
        this.externalTeleportTransitionsEnabled = GtaLikeTeleportConfig.isExternalTeleportTransitionsEnabled();
        this.fallbackChunkFadeEnabled = GtaLikeTeleportConfig.isFallbackChunkFadeEnabled();
    }

    private static GtaLikeTeleportConfig.ZoomDimension getInitialZoomDimension() {
        Minecraft client = Minecraft.getInstance();
        return GtaLikeTeleportConfig.ZoomDimension.fromLevel(client.level == null ? null : client.level.dimension());
    }

    private void loadZoomHeightState() {
        this.linked = GtaLikeTeleportConfig.areZoomHeightsLinked(this.selectedZoomDimension);
        this.zoomOutHeights = GtaLikeTeleportConfig.getZoomOutStageHeights(this.selectedZoomDimension);
        this.zoomInHeights = GtaLikeTeleportConfig.getRawZoomInStageHeights(this.selectedZoomDimension);
        if (this.linked) {
            this.zoomInHeights = this.zoomOutHeights.clone();
        }
    }

    @Override
    protected void init() {
        if (this.sessionLayoutBaseWidth <= 0 || this.sessionLayoutBaseHeight <= 0) {
            this.sessionLayoutBaseWidth = this.width;
            this.sessionLayoutBaseHeight = this.height;
        }
        resetWidgetReferences();
        LayoutRect panel = getContentRect();
        int y = getFirstSliderY(panel);

        if (this.currentPage == ConfigPage.ZOOM) {
            initZoomWidgets();
        } else if (this.currentPage == ConfigPage.GENERAL) {
            initGeneralWidgets();
        } else if (this.currentPage == ConfigPage.ZOOM_STAGE_2) {
            initAdvancedTwoWidgets();
        } else if (this.currentPage == ConfigPage.SOUNDS) {
            initSoundWidgets();
        } else if (this.currentPage == ConfigPage.OTHERS) {
            initOthersWidgets();
        }
        initPageNavigationWidgets();
        initPageTabWidgets();
        if (GtaLikeTeleportConfig.isConfigLayoutEditorButtonVisible()) {
            this.layoutDebugButton = addRenderableWidget(Button.builder(getLayoutDebugLabel(), button -> toggleLayoutDebug())
                    .bounds(panel.x + panel.width - 94, panel.y + 4, 90, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_debug.tooltip")))
                    .build());
        }
        if (this.layoutDebugEnabled) {
            this.aspectButton = addRenderableWidget(Button.builder(getAspectLabel(), button -> toggleAspectLock())
                    .bounds(panel.x + 4, panel.y + 4, 90, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_aspect.tooltip")))
                    .build());
            this.resetLayoutButton = addRenderableWidget(Button.builder(Component.translatable("gtalike_teleport.config.layout_reset"), button -> resetLayout())
                    .bounds(panel.x + 98, panel.y + 4, 98, 20)
                    .build());
            this.resetItemSizeButton = addRenderableWidget(Button.builder(Component.translatable("gtalike_teleport.config.layout_size_reset"), button -> resetSelectedItemSize())
                    .bounds(panel.x + 200, panel.y + 4, 88, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_size_reset.tooltip")))
                    .build());
            this.gridButton = addRenderableWidget(Button.builder(getGridLabel(), button -> toggleGrid())
                    .bounds(panel.x + 292, panel.y + 4, 78, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_grid.tooltip")))
                    .build());
            this.snapButton = addRenderableWidget(Button.builder(getSnapLabel(), button -> toggleSnap())
                    .bounds(panel.x + 282, panel.y + 4, 78, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_snap.tooltip")))
                    .build());
            this.textEditBox = addRenderableWidget(new EditBox(this.font, panel.x, y, 240, 20, Component.translatable("gtalike_teleport.config.text_editor")));
            this.textEditBox.setMaxLength(128);
            this.textEditBox.setResponder(value -> {
                if (!this.updatingTextEditor && itemSupportsText(this.selectedLayoutItem)) {
                    GtaLikeTeleportConfig.setConfigText(this.selectedLayoutItem, value);
                    applyItemText(this.selectedLayoutItem);
                }
            });
            this.layoutValueEditBox = addRenderableWidget(new EditBox(this.font, panel.x, y + 24, 270, 20, Component.translatable("gtalike_teleport.config.layout_values")));
            this.layoutValueEditBox.setMaxLength(80);
            this.applyLayoutValueButton = addRenderableWidget(Button.builder(Component.translatable("gtalike_teleport.config.layout_values_apply"), button -> applyLayoutValuesFromEditor())
                    .bounds(panel.x + 274, y + 24, 64, 20)
                    .tooltip(Tooltip.create(Component.translatable("gtalike_teleport.config.layout_values.tooltip")))
                    .build());
        }

        LayoutRect resetRect = getItemRect(ITEM_RESET_BUTTON);
        this.resetButton = addRenderableWidget(Button.builder(Component.empty(), button -> resetCurrentPage())
                .bounds(resetRect.x, resetRect.y, resetRect.width, resetRect.height)
                .build());
        LayoutRect doneRect = getItemRect(ITEM_DONE_BUTTON);
        this.doneButton = addRenderableWidget(Button.builder(Component.empty(), button -> onClose())
                .bounds(doneRect.x, doneRect.y, doneRect.width, doneRect.height)
                .build());
        repositionWidgets();
        refreshTextEditor();
        refreshLayoutValueEditor();
    }

    private void resetWidgetReferences() {
        this.zoomOutSlider = null;
        this.zoomInSlider = null;
        this.zoomStageGlideSlider = null;
        this.bodyHeightSlider = null;
        this.bodyGlideSlider = null;
        this.minecraftSoundVolumeSlider = null;
        this.customSoundVolumeSlider = null;
        this.zoomStageGlideTicksEditBox = null;
        this.zoomOutTicksEditBox = null;
        this.zoomInTicksEditBox = null;
        this.bodyGlideTicksEditBox = null;
        this.playerHideTicksEditBox = null;
        this.linkButton = null;
        this.prevPageButton = null;
        this.nextPageButton = null;
        this.pageTabButtons = new Button[0];
        this.effectToggleButton = null;
        this.movementToggleButton = null;
        this.crossDimensionTravelToggleButton = null;
        this.soundModeToggleButton = null;
        this.warpPlateToggleButton = null;
        this.externalTeleportToggleButton = null;
        this.fallbackChunkFadeToggleButton = null;
        this.resetButton = null;
        this.doneButton = null;
        this.layoutDebugButton = null;
        this.aspectButton = null;
        this.resetLayoutButton = null;
        this.resetItemSizeButton = null;
        this.gridButton = null;
        this.snapButton = null;
        this.textEditBox = null;
        this.layoutValueEditBox = null;
        this.applyLayoutValueButton = null;
    }

    private void initZoomWidgets() {
        LayoutRect outSliderRect = getItemRect(ITEM_ZOOM_OUT_SLIDER);
        this.zoomOutSlider = addRenderableWidget(new StageHeightSlider(
                outSliderRect.x,
                outSliderRect.y,
                outSliderRect.width,
                getItemComponent(ITEM_ZOOM_OUT_SLIDER),
                this.zoomOutHeights,
                values -> {
                    this.zoomOutHeights = values;
                    if (this.linked) {
                        this.zoomInHeights = values.clone();
                        if (this.zoomInSlider != null) {
                            this.zoomInSlider.setValues(this.zoomInHeights);
                        }
                    }
                    saveHeights();
                }
        ));
        if (this.linked) {
            this.zoomInHeights = this.zoomOutHeights.clone();
        }
        LayoutRect inSliderRect = getItemRect(ITEM_ZOOM_IN_SLIDER);
        this.zoomInSlider = addRenderableWidget(new StageHeightSlider(
                inSliderRect.x,
                inSliderRect.y,
                inSliderRect.width,
                getItemComponent(ITEM_ZOOM_IN_SLIDER),
                this.zoomInHeights,
                values -> {
                    if (!this.linked) {
                        this.zoomInHeights = values;
                        saveHeights();
                    }
                }
        ));
        this.zoomInSlider.setEditable(!this.linked);

        LayoutRect linkRect = getItemRect(ITEM_LINK_BUTTON);
        this.linkButton = addRenderableWidget(new LinkLockButton(linkRect.x, linkRect.y, linkRect.width, linkRect.height, this::toggleLinked));
        this.overworldDimensionButton = addDimensionButton(ITEM_DIMENSION_OVERWORLD, GtaLikeTeleportConfig.ZoomDimension.OVERWORLD, safeDefaultItemStack(Items.GRASS_BLOCK::getDefaultInstance), Component.literal("Overworld"), OVERWORLD_DIMENSION_ICON);
        this.netherDimensionButton = addDimensionButton(ITEM_DIMENSION_NETHER, GtaLikeTeleportConfig.ZoomDimension.NETHER, safeDefaultItemStack(Items.NETHERRACK::getDefaultInstance), Component.literal("The Nether"), NETHER_DIMENSION_ICON);
        this.endDimensionButton = addDimensionButton(ITEM_DIMENSION_END, GtaLikeTeleportConfig.ZoomDimension.END, safeDefaultItemStack(Items.END_STONE::getDefaultInstance), Component.literal("The End"), END_DIMENSION_ICON);
        updateLinkButton();
        updateDimensionButtons();
    }

    private static ItemStack safeDefaultItemStack(Supplier<ItemStack> stackFactory) {
        try {
            ItemStack stack = stackFactory.get();
            return stack == null ? ItemStack.EMPTY : stack;
        } catch (RuntimeException | LinkageError error) {
            return ItemStack.EMPTY;
        }
    }
    private DimensionIconButton addDimensionButton(String item, GtaLikeTeleportConfig.ZoomDimension dimension, ItemStack stack, Component label, ResourceLocation texture) {
        LayoutRect rect = getItemRect(item);
        return addRenderableWidget(new DimensionIconButton(
                rect.x,
                rect.y,
                rect.width,
                rect.height,
                stack,
                label,
                texture,
                () -> switchZoomDimension(dimension)
        ));
    }

    private void initGeneralWidgets() {
        LayoutRect effectRect = getItemRect(ITEM_EFFECT_TOGGLE);
        this.effectToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleEffectEnabled())
                .bounds(effectRect.x, effectRect.y, effectRect.width, effectRect.height)
                .build());

        LayoutRect movementRect = getItemRect(ITEM_MOVEMENT_TOGGLE);
        this.movementToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleMovementAllowed())
                .bounds(movementRect.x, movementRect.y, movementRect.width, movementRect.height)
                .build());

        LayoutRect crossDimensionRect = getItemRect(ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE);
        this.crossDimensionTravelToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleCrossDimensionTravel())
                .bounds(crossDimensionRect.x, crossDimensionRect.y, crossDimensionRect.width, crossDimensionRect.height)
                .build());
    }

    private void initOthersWidgets() {
        LayoutRect warpPlateRect = getItemRect(ITEM_WARP_PLATE_TOGGLE);
        this.warpPlateToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleWarpPlateTransitions())
                .bounds(warpPlateRect.x, warpPlateRect.y, warpPlateRect.width, warpPlateRect.height)
                .build());

        LayoutRect externalRect = getItemRect(ITEM_EXTERNAL_TELEPORT_TOGGLE);
        this.externalTeleportToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleExternalTeleportTransitions())
                .bounds(externalRect.x, externalRect.y, externalRect.width, externalRect.height)
                .build());

        updateOthersButtons();
    }
    private void initSoundWidgets() {
        LayoutRect modeRect = getItemRect(ITEM_SOUND_MODE_TOGGLE);
        this.soundModeToggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleCustomSoundsEnabled())
                .bounds(modeRect.x, modeRect.y, modeRect.width, modeRect.height)
                .build());

        LayoutRect minecraftVolumeRect = getItemRect(ITEM_MINECRAFT_VOLUME_SLIDER);
        this.minecraftSoundVolumeSlider = addRenderableWidget(new SingleValueSlider(
                minecraftVolumeRect.x,
                minecraftVolumeRect.y,
                minecraftVolumeRect.width,
                getItemComponent(ITEM_MINECRAFT_VOLUME_SLIDER),
                this.minecraftSoundVolume,
                GtaLikeTeleportConfig.getMinSoundVolume(),
                GtaLikeTeleportConfig.getMaxSoundVolume(),
                0.1D,
                false,
                "x",
                value -> {
                    this.minecraftSoundVolume = value;
                    GtaLikeTeleportConfig.setMinecraftSoundVolume(value);
                }
        ));

        LayoutRect customVolumeRect = getItemRect(ITEM_CUSTOM_VOLUME_SLIDER);
        this.customSoundVolumeSlider = addRenderableWidget(new SingleValueSlider(
                customVolumeRect.x,
                customVolumeRect.y,
                customVolumeRect.width,
                getItemComponent(ITEM_CUSTOM_VOLUME_SLIDER),
                this.customSoundVolume,
                GtaLikeTeleportConfig.getMinSoundVolume(),
                GtaLikeTeleportConfig.getMaxSoundVolume(),
                0.1D,
                false,
                "x",
                value -> {
                    this.customSoundVolume = value;
                    GtaLikeTeleportConfig.setCustomSoundVolume(value);
                }
        ));
        updateSoundButtons();
    }

    private void initAdvancedOneWidgets() {
        LayoutRect glideRect = getItemRect(ITEM_ZOOM_STAGE_GLIDE_SLIDER);
        this.zoomStageGlideSlider = addRenderableWidget(new SingleValueSlider(
                glideRect.x,
                glideRect.y,
                glideRect.width,
                getItemComponent(ITEM_ZOOM_STAGE_GLIDE_SLIDER),
                this.zoomStageGlideHeight,
                GtaLikeTeleportConfig.getMinZoomStageGlideHeight(),
                GtaLikeTeleportConfig.getMaxZoomStageGlideHeight(),
                0.1D,
                false,
                " blocks",
                value -> {
                    this.zoomStageGlideHeight = value;
                    GtaLikeTeleportConfig.setZoomStageGlideHeight(value);
                }
        ));
        LayoutRect ticksRect = getItemRect(ITEM_ZOOM_STAGE_GLIDE_TICKS_FIELD);
        this.zoomStageGlideTicksEditBox = addTickEditBox(ticksRect, this.zoomStageGlideTicks, value -> {
            this.zoomStageGlideTicks = value;
            GtaLikeTeleportConfig.setZoomStageGlideTicks(value);
        });
    }

    private void initAdvancedTwoWidgets() {
        LayoutRect outRect = getItemRect(ITEM_ZOOM_OUT_TICKS_FIELD);
        this.zoomOutTicksEditBox = addStageTicksEditBox(outRect, this.zoomOutStageTicks, values -> {
            this.zoomOutStageTicks = values;
            saveStageTicks();
        });
        LayoutRect inRect = getItemRect(ITEM_ZOOM_IN_TICKS_FIELD);
        this.zoomInTicksEditBox = addStageTicksEditBox(inRect, this.zoomInStageTicks, values -> {
            this.zoomInStageTicks = values;
            saveStageTicks();
        });
    }

    private void initAdvancedThreeWidgets() {
        LayoutRect bodyRect = getItemRect(ITEM_BODY_HEIGHT_SLIDER);
        this.bodyHeightSlider = addRenderableWidget(new SingleValueSlider(
                bodyRect.x,
                bodyRect.y,
                bodyRect.width,
                getItemComponent(ITEM_BODY_HEIGHT_SLIDER),
                this.bodyCameraHeight,
                GtaLikeTeleportConfig.getMinBodyCameraHeight(),
                GtaLikeTeleportConfig.getMaxBodyCameraHeight(),
                0.1D,
                false,
                " blocks",
                value -> {
                    this.bodyCameraHeight = value;
                    GtaLikeTeleportConfig.setBodyCameraHeight(value);
                }
        ));
        LayoutRect glideRect = getItemRect(ITEM_BODY_GLIDE_SLIDER);
        this.bodyGlideSlider = addRenderableWidget(new SingleValueSlider(
                glideRect.x,
                glideRect.y,
                glideRect.width,
                getItemComponent(ITEM_BODY_GLIDE_SLIDER),
                this.bodyGlideHeight,
                GtaLikeTeleportConfig.getMinBodyGlideHeight(),
                GtaLikeTeleportConfig.getMaxBodyGlideHeight(),
                0.1D,
                false,
                " blocks",
                value -> {
                    this.bodyGlideHeight = value;
                    GtaLikeTeleportConfig.setBodyGlideHeight(value);
                }
        ));
        LayoutRect ticksRect = getItemRect(ITEM_BODY_GLIDE_TICKS_FIELD);
        this.bodyGlideTicksEditBox = addTickEditBox(ticksRect, this.bodyGlideTicks, value -> {
            this.bodyGlideTicks = value;
            GtaLikeTeleportConfig.setBodyGlideTicks(value);
        });
        LayoutRect hideRect = getItemRect(ITEM_PLAYER_HIDE_TICKS_FIELD);
        this.playerHideTicksEditBox = addLocalPlayerHideTicksEditBox(hideRect, this.localPlayerHideTicks, value -> {
            this.localPlayerHideTicks = value;
            GtaLikeTeleportConfig.setLocalPlayerHideTicks(value);
        });
    }

    private EditBox addTickEditBox(LayoutRect rect, int value, Consumer<Integer> onChanged) {
        EditBox editBox = addRenderableWidget(new ScaledEditBox(this.font, rect.x, rect.y, rect.width, rect.height, Component.literal("ticks"), getContentScale()));
        editBox.setMaxLength(8);
        setEditBoxValue(editBox, Integer.toString(value));
        editBox.setResponder(text -> {
            if (this.updatingValueWidgets) {
                return;
            }
            Integer parsed = parseSingleTick(text);
            if (parsed != null) {
                onChanged.accept(parsed);
            }
        });
        return editBox;
    }

    private EditBox addLocalPlayerHideTicksEditBox(LayoutRect rect, int value, Consumer<Integer> onChanged) {
        EditBox editBox = addRenderableWidget(new ScaledEditBox(this.font, rect.x, rect.y, rect.width, rect.height, Component.literal("hide ticks"), getContentScale()));
        editBox.setMaxLength(4);
        setEditBoxValue(editBox, Integer.toString(value));
        editBox.setResponder(text -> {
            if (this.updatingValueWidgets) {
                return;
            }
            Integer parsed = parseLocalPlayerHideTicks(text);
            if (parsed != null) {
                onChanged.accept(parsed);
            }
        });
        return editBox;
    }

    private EditBox addStageTicksEditBox(LayoutRect rect, int[] values, Consumer<int[]> onChanged) {
        EditBox editBox = addRenderableWidget(new ScaledEditBox(this.font, rect.x, rect.y, rect.width, rect.height, Component.literal("stage ticks"), getContentScale()));
        editBox.setMaxLength(32);
        setEditBoxValue(editBox, formatStageTicks(values));
        editBox.setResponder(text -> {
            if (this.updatingValueWidgets) {
                return;
            }
            int[] parsed = parseStageTicks(text);
            if (parsed != null) {
                onChanged.accept(parsed);
            }
        });
        return editBox;
    }

    private void setEditBoxValue(EditBox editBox, String value) {
        if (editBox == null || value.equals(editBox.getValue())) {
            return;
        }
        this.updatingValueWidgets = true;
        editBox.setValue(value);
        this.updatingValueWidgets = false;
    }

    private Integer parseSingleTick(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return GtaLikeTeleportConfig.sanitizeStageTicksValue(Integer.parseInt(text.trim()));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Integer parseLocalPlayerHideTicks(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return GtaLikeTeleportConfig.sanitizeLocalPlayerHideTicks(Integer.parseInt(text.trim()));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int[] parseStageTicks(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = text.replace('/', ' ').replace(',', ' ').replace(';', ' ');
        String[] tokens = normalized.trim().split("\\s+");
        if (tokens.length != 3) {
            return null;
        }
        int[] values = new int[3];
        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.parseInt(tokens[i]);
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return GtaLikeTeleportConfig.sanitizeStageTicks(values);
    }

    private String formatStageTicks(int[] values) {
        int[] sanitized = GtaLikeTeleportConfig.sanitizeStageTicks(values);
        return sanitized[0] + " / " + sanitized[1] + " / " + sanitized[2];
    }

    private void initPageNavigationWidgets() {
        LayoutRect prevRect = getItemRect(ITEM_PREV_PAGE_BUTTON);
        this.prevPageButton = addRenderableWidget(Button.builder(Component.empty(), button -> switchPage(-1))
                .bounds(prevRect.x, prevRect.y, prevRect.width, prevRect.height)
                .build());
        LayoutRect nextRect = getItemRect(ITEM_NEXT_PAGE_BUTTON);
        this.nextPageButton = addRenderableWidget(Button.builder(Component.empty(), button -> switchPage(1))
                .bounds(nextRect.x, nextRect.y, nextRect.width, nextRect.height)
                .build());
    }

    private void initPageTabWidgets() {
        ConfigPage[] pages = ConfigPage.values();
        this.pageTabButtons = new Button[pages.length];
        for (int i = 0; i < pages.length; i++) {
            ConfigPage page = pages[i];
            LayoutRect rect = getItemRect(page.tabItem);
            Button button = addRenderableWidget(Button.builder(Component.empty(), clicked -> switchToPage(page))
                    .bounds(rect.x, rect.y, rect.width, rect.height)
                    .build());
            button.active = page != this.currentPage;
            this.pageTabButtons[i] = button;
        }
    }
    public void renderBackground(GuiGraphics context) {
        if (this.minecraft == null || this.minecraft.level == null) {
            context.fill(0, 0, this.width, this.height, 0xFF000000);
        }
        context.fill(0, 0, this.width, this.height, 0x99000000);

        LayoutRect outer = toOuterRect(getContentRect());
        context.fill(outer.x, outer.y, outer.right(), outer.bottom(), PANEL_COLOR);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, tickProgress);

        double contentScale = getContentScale();
        for (String item : getVisibleLayoutItems()) {
            if (!isManualTextItem(item)) {
                continue;
            }
            LayoutRect rect = getItemRect(item);
            int color = isMutedTextItem(item) ? MUTED_TEXT_COLOR : TEXT_COLOR;
            if (isCenteredTextItem(item)) {
                drawScaledCenteredText(context, this.font, getItemComponent(item), rect.x + rect.width / 2, rect.y, color, contentScale);
            } else {
                drawScaledText(context, this.font, getItemComponent(item), rect.x, rect.y, color, contentScale);
            }
        }

        for (String item : getVisibleLayoutItems()) {
            if (isManualButtonTextItem(item)) {
                drawButtonText(context, item);
            }
        }

        renderPanelOutline(context);

        if (this.layoutDebugEnabled) {
            LayoutRect panel = getContentRect();
            if (this.layoutGridEnabled) {
                extractLayoutGuides(context);
            }
            context.drawString(this.font, Component.translatable("gtalike_teleport.config.layout_debug_hint"), panel.x, panel.y + panel.height - 58, 0xFFFFFF55);
            extractLayoutDebugOverlays(context);
        }
    }


    private void renderPanelOutline(GuiGraphics context) {
        LayoutRect outer = toOuterRect(getContentRect());
        int color = this.layoutDebugEnabled ? DEBUG_BORDER_COLOR : PANEL_BORDER_COLOR;
        context.fill(outer.x, outer.y, outer.x + 1, outer.bottom(), color);
        context.fill(outer.right() - 1, outer.y, outer.right(), outer.bottom(), color);
        context.fill(outer.x, outer.bottom() - 1, outer.right(), outer.bottom(), color);
        if (this.layoutDebugEnabled) {
            context.fill(outer.x, outer.y, outer.right(), outer.y + 1, color);
        }
    }
    private static void drawScaledText(GuiGraphics context, net.minecraft.client.gui.Font font, Component text, int x, int y, int color, double scale) {
        if (Math.abs(scale - 1.0D) < 0.005D) {
            context.drawString(font, text, x, y, color);
            return;
        }

        context.pose().pushPose();
        context.pose().translate((float) x, (float) y, 0.0F);
        context.pose().scale((float) scale, (float) scale, 1.0F);
        context.drawString(font, text, 0, 0, color);
        context.pose().popPose();
    }

    private static void drawScaledCenteredText(GuiGraphics context, net.minecraft.client.gui.Font font, Component text, int centerX, int y, int color, double scale) {
        if (Math.abs(scale - 1.0D) < 0.005D) {
            context.drawCenteredString(font, text, centerX, y, color);
            return;
        }

        int x = centerX - (int) Math.round(font.width(text) * scale / 2.0D);
        drawScaledText(context, font, text, x, y, color, scale);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        normalizeValueEditorsForClick(mouseX, mouseY);
        if (this.layoutDebugEnabled && isDebugControlAt(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (this.layoutDebugEnabled) {
            LayoutEditAction resizeAction = getResizeAction(this.selectedLayoutItem, mouseX, mouseY);
            if (resizeAction != LayoutEditAction.NONE) {
                beginLayoutEdit(this.selectedLayoutItem, resizeAction, mouseX, mouseY);
                return true;
            }

            String hitItem = findLayoutItemAt(mouseX, mouseY);
            if (hitItem != null) {
                selectLayoutItem(hitItem);
                beginLayoutEdit(hitItem, LayoutEditAction.MOVE, mouseX, mouseY);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.layoutEditAction != LayoutEditAction.NONE) {
            updateLayoutEdit(mouseX, mouseY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.layoutEditAction != LayoutEditAction.NONE) {
            finishLayoutEdit();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        normalizeValueEditors();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private void switchPage(int direction) {
        ConfigPage[] pages = ConfigPage.values();
        int nextIndex = Math.floorMod(this.currentPage.ordinal() + direction, pages.length);
        switchToPage(pages[nextIndex]);
    }

    private void switchToPage(ConfigPage nextPage) {
        if (nextPage == this.currentPage) {
            return;
        }

        this.currentPage = nextPage;
        this.layoutEditAction = LayoutEditAction.NONE;
        this.editingLayoutItem = null;
        this.editingRect = null;
        if (!isVisibleLayoutItem(this.selectedLayoutItem)) {
            this.selectedLayoutItem = ITEM_PANEL;
        }
        rebuildWidgets();
    }

    private void normalizeValueEditorsForClick(double mouseX, double mouseY) {
        if (this.zoomStageGlideTicksEditBox != null && this.zoomStageGlideTicksEditBox.isFocused() && !this.zoomStageGlideTicksEditBox.isMouseOver(mouseX, mouseY)) {
            setEditBoxValue(this.zoomStageGlideTicksEditBox, Integer.toString(this.zoomStageGlideTicks));
        }
        if (this.zoomOutTicksEditBox != null && this.zoomOutTicksEditBox.isFocused() && !this.zoomOutTicksEditBox.isMouseOver(mouseX, mouseY)) {
            setEditBoxValue(this.zoomOutTicksEditBox, formatStageTicks(this.zoomOutStageTicks));
        }
        if (this.zoomInTicksEditBox != null && this.zoomInTicksEditBox.isFocused() && !this.zoomInTicksEditBox.isMouseOver(mouseX, mouseY)) {
            setEditBoxValue(this.zoomInTicksEditBox, formatStageTicks(this.zoomInStageTicks));
        }
        if (this.bodyGlideTicksEditBox != null && this.bodyGlideTicksEditBox.isFocused() && !this.bodyGlideTicksEditBox.isMouseOver(mouseX, mouseY)) {
            setEditBoxValue(this.bodyGlideTicksEditBox, Integer.toString(this.bodyGlideTicks));
        }
        if (this.playerHideTicksEditBox != null && this.playerHideTicksEditBox.isFocused() && !this.playerHideTicksEditBox.isMouseOver(mouseX, mouseY)) {
            setEditBoxValue(this.playerHideTicksEditBox, Integer.toString(this.localPlayerHideTicks));
        }
    }

    private void normalizeValueEditors() {
        setEditBoxValue(this.zoomStageGlideTicksEditBox, Integer.toString(this.zoomStageGlideTicks));
        setEditBoxValue(this.zoomOutTicksEditBox, formatStageTicks(this.zoomOutStageTicks));
        setEditBoxValue(this.zoomInTicksEditBox, formatStageTicks(this.zoomInStageTicks));
        setEditBoxValue(this.bodyGlideTicksEditBox, Integer.toString(this.bodyGlideTicks));
        setEditBoxValue(this.playerHideTicksEditBox, Integer.toString(this.localPlayerHideTicks));
    }

    private void toggleEffectEnabled() {
        this.effectEnabled = !this.effectEnabled;
        GtaLikeTeleportConfig.setEffectEnabled(this.effectEnabled);
        updateGeneralButtons();
    }

    private void toggleMovementAllowed() {
        this.movementAllowed = !this.movementAllowed;
        GtaLikeTeleportConfig.setPlayerFreezeEnabled(!this.movementAllowed);
        updateGeneralButtons();
    }

    private void toggleCrossDimensionTravel() {
        this.crossDimensionTravelEnabled = !this.crossDimensionTravelEnabled;
        GtaLikeTeleportConfig.setCrossDimensionTravelEnabled(this.crossDimensionTravelEnabled);
        updateGeneralButtons();
    }

    private void toggleWarpPlateTransitions() {
        this.warpPlateTransitionsEnabled = !this.warpPlateTransitionsEnabled;
        GtaLikeTeleportConfig.setWarpPlateTransitionsEnabled(this.warpPlateTransitionsEnabled);
        updateOthersButtons();
    }

    private void toggleExternalTeleportTransitions() {
        if (!isExternalTeleportToggleAvailable()) {
            updateOthersButtons();
            return;
        }
        this.externalTeleportTransitionsEnabled = !this.externalTeleportTransitionsEnabled;
        GtaLikeTeleportConfig.setExternalTeleportTransitionsEnabled(this.externalTeleportTransitionsEnabled);
        updateOthersButtons();
    }

    private void toggleFallbackChunkFade() {
        this.fallbackChunkFadeEnabled = !this.fallbackChunkFadeEnabled;
        GtaLikeTeleportConfig.setFallbackChunkFadeEnabled(this.fallbackChunkFadeEnabled);
        updateOthersButtons();
    }

    private boolean isExternalTeleportToggleAvailable() {
        return GtaLikeTeleportClientNetworking.isServerSideTeleportAvailable();
    }

    private void updateOthersButtons() {
        if (this.warpPlateToggleButton != null) {
            this.warpPlateToggleButton.setMessage(Component.empty());
        }
        if (this.externalTeleportToggleButton != null) {
            this.externalTeleportToggleButton.active = isExternalTeleportToggleAvailable();
            this.externalTeleportToggleButton.setMessage(Component.empty());
        }
    }

    private void toggleCustomSoundsEnabled() {
        this.customSoundsEnabled = !this.customSoundsEnabled;
        GtaLikeTeleportConfig.setCustomSoundsEnabled(this.customSoundsEnabled);
        updateSoundButtons();
    }

    private void updateScaledEditBox(EditBox editBox) {
        if (editBox instanceof ScaledEditBox scaledEditBox) {
            scaledEditBox.setTextScale(getContentScale());
        }
    }

    private void updateSoundButtons() {
        if (this.soundModeToggleButton != null) {
            this.soundModeToggleButton.setMessage(Component.empty());
        }
    }

    private void updateGeneralButtons() {
        if (this.effectToggleButton != null) {
            this.effectToggleButton.setMessage(Component.empty());
        }
        if (this.movementToggleButton != null) {
            this.movementToggleButton.setMessage(Component.empty());
        }
        if (this.crossDimensionTravelToggleButton != null) {
            this.crossDimensionTravelToggleButton.setMessage(Component.empty());
        }
    }

    private void resetSoundSettings() {
        this.customSoundsEnabled = false;
        this.minecraftSoundVolume = GtaLikeTeleportConfig.getDefaultMinecraftSoundVolume();
        this.customSoundVolume = GtaLikeTeleportConfig.getDefaultCustomSoundVolume();
        GtaLikeTeleportConfig.setCustomSoundsEnabled(this.customSoundsEnabled);
        GtaLikeTeleportConfig.setMinecraftSoundVolume(this.minecraftSoundVolume);
        GtaLikeTeleportConfig.setCustomSoundVolume(this.customSoundVolume);
        rebuildWidgets();
    }

    private void resetCurrentPage() {
        if (this.currentPage == ConfigPage.ZOOM) {
            resetHeights();
            return;
        }
        if (this.currentPage == ConfigPage.GENERAL) {
            this.effectEnabled = true;
            this.movementAllowed = false;
            this.crossDimensionTravelEnabled = false;
            GtaLikeTeleportConfig.setEffectEnabled(true);
            GtaLikeTeleportConfig.setPlayerFreezeEnabled(true);
            GtaLikeTeleportConfig.setCrossDimensionTravelEnabled(false);
            updateGeneralButtons();
            return;
        }
        if (this.currentPage == ConfigPage.ZOOM_STAGE_2) {
            this.zoomOutStageTicks = GtaLikeTeleportConfig.getDefaultStageTicks();
            this.zoomInStageTicks = GtaLikeTeleportConfig.getDefaultStageTicks();
            saveStageTicks();
            rebuildWidgets();
            return;
        }
        if (this.currentPage == ConfigPage.SOUNDS) {
            resetSoundSettings();
            return;
        }
        if (this.currentPage == ConfigPage.OTHERS) {
            this.warpPlateTransitionsEnabled = true;
            this.externalTeleportTransitionsEnabled = true;
            this.fallbackChunkFadeEnabled = false;
            GtaLikeTeleportConfig.setWarpPlateTransitionsEnabled(true);
            GtaLikeTeleportConfig.setExternalTeleportTransitionsEnabled(true);
            GtaLikeTeleportConfig.setFallbackChunkFadeEnabled(false);
            updateOthersButtons();
        }
    }
    private void toggleLinked() {
        this.linked = !this.linked;
        if (this.linked) {
            this.zoomInHeights = this.zoomOutHeights.clone();
        }
        if (!isVisibleLayoutItem(this.selectedLayoutItem)) {
            this.selectedLayoutItem = ITEM_PANEL;
        }
        saveHeights();
        rebuildWidgets();
    }

    private void toggleLayoutDebug() {
        this.layoutDebugEnabled = !this.layoutDebugEnabled;
        GtaLikeTeleportConfig.setConfigLayoutDebugEnabled(this.layoutDebugEnabled);
        this.layoutEditAction = LayoutEditAction.NONE;
        this.editingLayoutItem = null;
        this.editingRect = null;
        if (this.selectedLayoutItem == null) {
            this.selectedLayoutItem = ITEM_PANEL;
        }
        rebuildWidgets();
    }

    private void toggleAspectLock() {
        this.layoutAspectLocked = !this.layoutAspectLocked;
        GtaLikeTeleportConfig.setConfigLayoutAspectLocked(this.layoutAspectLocked);
        if (this.aspectButton != null) {
            this.aspectButton.setMessage(getAspectLabel());
        }
    }

    private void toggleGrid() {
        this.layoutGridEnabled = !this.layoutGridEnabled;
        GtaLikeTeleportConfig.setConfigLayoutGridEnabled(this.layoutGridEnabled);
        if (this.gridButton != null) {
            this.gridButton.setMessage(getGridLabel());
        }
    }

    private void toggleSnap() {
        this.layoutSnapEnabled = !this.layoutSnapEnabled;
        GtaLikeTeleportConfig.setConfigLayoutSnapEnabled(this.layoutSnapEnabled);
        if (this.snapButton != null) {
            this.snapButton.setMessage(getSnapLabel());
        }
    }

    private void resetLayout() {
        GtaLikeTeleportConfig.resetConfigLayout();
        GtaLikeTeleportConfig.resetConfigWidgetLayouts();
        this.selectedLayoutItem = ITEM_PANEL;
        this.editingLayoutItem = null;
        this.editingRect = null;
        rebuildWidgets();
    }

    private void resetSelectedItemSize() {
        if (this.selectedLayoutItem == null || !isVisibleLayoutItem(this.selectedLayoutItem)) {
            return;
        }

        String item = this.selectedLayoutItem;
        LayoutRect current = getEditableRect(item);
        LayoutRect resetRect;
        if (ITEM_PANEL.equals(item)) {
            LayoutRect defaults = getDefaultContentRect();
            resetRect = constrainContentRect(new LayoutRect(
                    current.centerX() - defaults.width / 2,
                    current.centerY() - defaults.height / 2,
                    defaults.width,
                    defaults.height
            ));
        } else {
            LayoutRect defaults = getDefaultItemRect(item, getContentRect());
            resetRect = constrainItemRect(item, new LayoutRect(current.x, current.y, defaults.width, defaults.height));
        }

        saveLayoutRect(item, resetRect);
        this.editingLayoutItem = null;
        this.editingRect = null;
        this.layoutEditAction = LayoutEditAction.NONE;
        repositionWidgets();
        refreshLayoutValueEditor();
    }

    private void applyLayoutValuesFromEditor() {
        if (this.layoutValueEditBox == null || this.selectedLayoutItem == null || !isVisibleLayoutItem(this.selectedLayoutItem)) {
            return;
        }

        double[] values = parseLayoutValues(this.layoutValueEditBox.getValue());
        if (values == null) {
            refreshLayoutValueEditor();
            return;
        }

        String item = this.selectedLayoutItem;
        LayoutRect rect;
        if (ITEM_PANEL.equals(item)) {
            rect = constrainContentRect(new LayoutRect(
                    (int) Math.round(values[0] * this.width),
                    (int) Math.round(values[1] * this.height),
                    (int) Math.round(values[2] * this.width),
                    (int) Math.round(values[3] * this.height)
            ));
        } else {
            LayoutRect panel = getContentRect();
            rect = constrainItemRect(item, new LayoutRect(
                    panel.x + (int) Math.round(values[0] * panel.width),
                    panel.y + (int) Math.round(values[1] * panel.height),
                    (int) Math.round(values[2] * panel.width),
                    (int) Math.round(values[3] * panel.height)
            ));
        }

        saveLayoutRect(item, rect);
        this.editingLayoutItem = null;
        this.editingRect = null;
        this.layoutEditAction = LayoutEditAction.NONE;
        repositionWidgets();
        refreshLayoutValueEditor();
    }

    private double[] parseLayoutValues(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        double[] values = new double[]{Double.NaN, Double.NaN, Double.NaN, Double.NaN};
        String normalized = text.replace(',', ' ').replace(';', ' ');
        String[] tokens = normalized.trim().split("\\s+");
        int sequentialIndex = 0;
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            int equals = token.indexOf('=');
            try {
                if (equals > 0) {
                    String name = token.substring(0, equals).trim().toLowerCase(Locale.ROOT);
                    double value = Double.parseDouble(token.substring(equals + 1).trim());
                    if ("x".equals(name)) {
                        values[0] = value;
                    } else if ("y".equals(name)) {
                        values[1] = value;
                    } else if ("w".equals(name) || "width".equals(name)) {
                        values[2] = value;
                    } else if ("h".equals(name) || "height".equals(name)) {
                        values[3] = value;
                    }
                } else if (sequentialIndex < values.length) {
                    values[sequentialIndex] = Double.parseDouble(token.trim());
                    sequentialIndex++;
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        for (double value : values) {
            if (!Double.isFinite(value)) {
                return null;
            }
        }
        return values;
    }

    private void saveLayoutRect(String item, LayoutRect rect) {
        if (ITEM_PANEL.equals(item)) {
            if (this.width > 0 && this.height > 0) {
                GtaLikeTeleportConfig.setConfigLayout(
                        rect.x / (double) this.width,
                        rect.y / (double) this.height,
                        rect.width / (double) this.width,
                        rect.height / (double) this.height,
                        this.width,
                        this.height
                );
            }
            return;
        }

        LayoutRect panel = getContentRect();
        if (panel.width > 0 && panel.height > 0) {
            GtaLikeTeleportConfig.setConfigWidgetLayout(
                    item,
                    (rect.x - panel.x) / (double) panel.width,
                    (rect.y - panel.y) / (double) panel.height,
                    rect.width / (double) panel.width,
                    rect.height / (double) panel.height,
                    panel.width,
                    panel.height
            );
        }
    }

    private void resetHeights() {
        this.zoomOutHeights = GtaLikeTeleportConfig.getDefaultStageHeights();
        this.zoomInHeights = GtaLikeTeleportConfig.getDefaultStageHeights();
        saveHeights();
        rebuildWidgets();
    }

    private void saveHeights() {
        this.zoomOutHeights = GtaLikeTeleportConfig.sanitizeStageHeights(this.zoomOutHeights);
        this.zoomInHeights = GtaLikeTeleportConfig.sanitizeStageHeights(this.zoomInHeights);
        GtaLikeTeleportConfig.setZoomStageHeights(this.selectedZoomDimension, this.linked, this.zoomOutHeights, this.zoomInHeights);
    }

    private void switchZoomDimension(GtaLikeTeleportConfig.ZoomDimension dimension) {
        if (dimension == null || dimension == this.selectedZoomDimension) {
            return;
        }
        this.selectedZoomDimension = dimension;
        loadZoomHeightState();
        this.layoutEditAction = LayoutEditAction.NONE;
        this.editingLayoutItem = null;
        this.editingRect = null;
        if (!isVisibleLayoutItem(this.selectedLayoutItem)) {
            this.selectedLayoutItem = ITEM_PANEL;
        }
        rebuildWidgets();
    }

    private void saveStageTicks() {
        this.zoomOutStageTicks = GtaLikeTeleportConfig.sanitizeStageTicks(this.zoomOutStageTicks);
        this.zoomInStageTicks = GtaLikeTeleportConfig.sanitizeStageTicks(this.zoomInStageTicks);
        GtaLikeTeleportConfig.setZoomStageTicks(this.zoomOutStageTicks, this.zoomInStageTicks);
    }

    private void updateLinkButton() {
        if (this.linkButton == null) {
            return;
        }

        this.linkButton.setLocked(this.linked);
        this.linkButton.setTooltip(Tooltip.create(Component.translatable(
                this.linked ? "gtalike_teleport.config.linked.tooltip" : "gtalike_teleport.config.unlinked.tooltip"
        )));
    }

    private void repositionDimensionButton(DimensionIconButton button, String item, GtaLikeTeleportConfig.ZoomDimension dimension) {
        if (button == null) {
            return;
        }
        LayoutRect rect = getItemRect(item);
        setWidgetRectangle(button, rect.width, rect.height, rect.x, rect.y);
        button.setSelected(this.selectedZoomDimension == dimension);
    }

    private void updateDimensionButtons() {
        repositionDimensionButton(this.overworldDimensionButton, ITEM_DIMENSION_OVERWORLD, GtaLikeTeleportConfig.ZoomDimension.OVERWORLD);
        repositionDimensionButton(this.netherDimensionButton, ITEM_DIMENSION_NETHER, GtaLikeTeleportConfig.ZoomDimension.NETHER);
        repositionDimensionButton(this.endDimensionButton, ITEM_DIMENSION_END, GtaLikeTeleportConfig.ZoomDimension.END);
    }

    private void repositionWidgets() {
        for (String item : getVisibleLayoutItems()) {
            applyItemText(item);
        }

        if (this.zoomOutSlider != null) {
            LayoutRect rect = getItemRect(ITEM_ZOOM_OUT_SLIDER);
            setWidgetRectangle(this.zoomOutSlider, rect.width, rect.height, rect.x, rect.y);
        }
        if (this.zoomInSlider != null) {
            if (this.linked) {
                this.zoomInHeights = this.zoomOutHeights.clone();
                this.zoomInSlider.setValues(this.zoomInHeights);
            }
            this.zoomInSlider.setEditable(!this.linked);
            LayoutRect rect = getItemRect(ITEM_ZOOM_IN_SLIDER);
            setWidgetRectangle(this.zoomInSlider, rect.width, rect.height, rect.x, rect.y);
        }
        if (this.linkButton != null) {
            LayoutRect rect = getItemRect(ITEM_LINK_BUTTON);
            setWidgetRectangle(this.linkButton, rect.width, rect.height, rect.x, rect.y);
        }
        repositionDimensionButton(this.overworldDimensionButton, ITEM_DIMENSION_OVERWORLD, GtaLikeTeleportConfig.ZoomDimension.OVERWORLD);
        repositionDimensionButton(this.netherDimensionButton, ITEM_DIMENSION_NETHER, GtaLikeTeleportConfig.ZoomDimension.NETHER);
        repositionDimensionButton(this.endDimensionButton, ITEM_DIMENSION_END, GtaLikeTeleportConfig.ZoomDimension.END);
        if (this.effectToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_EFFECT_TOGGLE);
            setWidgetRectangle(this.effectToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.effectToggleButton.setMessage(Component.empty());
        }
        if (this.movementToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_MOVEMENT_TOGGLE);
            setWidgetRectangle(this.movementToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.movementToggleButton.setMessage(Component.empty());
        }
        if (this.crossDimensionTravelToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE);
            setWidgetRectangle(this.crossDimensionTravelToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.crossDimensionTravelToggleButton.setMessage(Component.empty());
        }
        if (this.soundModeToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_SOUND_MODE_TOGGLE);
            setWidgetRectangle(this.soundModeToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.soundModeToggleButton.setMessage(Component.empty());
        }
        if (this.warpPlateToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_WARP_PLATE_TOGGLE);
            setWidgetRectangle(this.warpPlateToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.warpPlateToggleButton.setMessage(Component.empty());
        }
        if (this.externalTeleportToggleButton != null) {
            LayoutRect rect = getItemRect(ITEM_EXTERNAL_TELEPORT_TOGGLE);
            setWidgetRectangle(this.externalTeleportToggleButton, rect.width, rect.height, rect.x, rect.y);
            this.externalTeleportToggleButton.active = isExternalTeleportToggleAvailable();
            this.externalTeleportToggleButton.setMessage(Component.empty());
        }
        if (this.minecraftSoundVolumeSlider != null) {
            LayoutRect rect = getItemRect(ITEM_MINECRAFT_VOLUME_SLIDER);
            setWidgetRectangle(this.minecraftSoundVolumeSlider, rect.width, rect.height, rect.x, rect.y);
            this.minecraftSoundVolumeSlider.setValue(this.minecraftSoundVolume);
        }
        if (this.customSoundVolumeSlider != null) {
            LayoutRect rect = getItemRect(ITEM_CUSTOM_VOLUME_SLIDER);
            setWidgetRectangle(this.customSoundVolumeSlider, rect.width, rect.height, rect.x, rect.y);
            this.customSoundVolumeSlider.setValue(this.customSoundVolume);
        }
        if (this.zoomStageGlideSlider != null) {
            LayoutRect rect = getItemRect(ITEM_ZOOM_STAGE_GLIDE_SLIDER);
            setWidgetRectangle(this.zoomStageGlideSlider, rect.width, rect.height, rect.x, rect.y);
            this.zoomStageGlideSlider.setValue(this.zoomStageGlideHeight);
        }
        if (this.zoomStageGlideTicksEditBox != null) {
            LayoutRect rect = getItemRect(ITEM_ZOOM_STAGE_GLIDE_TICKS_FIELD);
            setWidgetRectangle(this.zoomStageGlideTicksEditBox, rect.width, rect.height, rect.x, rect.y);
            updateScaledEditBox(this.zoomStageGlideTicksEditBox);
            this.zoomStageGlideTicksEditBox.setEditable(!this.layoutDebugEnabled);
            if (!this.zoomStageGlideTicksEditBox.isFocused()) {
                setEditBoxValue(this.zoomStageGlideTicksEditBox, Integer.toString(this.zoomStageGlideTicks));
            }
        }
        if (this.zoomOutTicksEditBox != null) {
            LayoutRect rect = getItemRect(ITEM_ZOOM_OUT_TICKS_FIELD);
            setWidgetRectangle(this.zoomOutTicksEditBox, rect.width, rect.height, rect.x, rect.y);
            updateScaledEditBox(this.zoomOutTicksEditBox);
            this.zoomOutTicksEditBox.setEditable(!this.layoutDebugEnabled);
            if (!this.zoomOutTicksEditBox.isFocused()) {
                setEditBoxValue(this.zoomOutTicksEditBox, formatStageTicks(this.zoomOutStageTicks));
            }
        }
        if (this.zoomInTicksEditBox != null) {
            LayoutRect rect = getItemRect(ITEM_ZOOM_IN_TICKS_FIELD);
            setWidgetRectangle(this.zoomInTicksEditBox, rect.width, rect.height, rect.x, rect.y);
            updateScaledEditBox(this.zoomInTicksEditBox);
            this.zoomInTicksEditBox.setEditable(!this.layoutDebugEnabled);
            if (!this.zoomInTicksEditBox.isFocused()) {
                setEditBoxValue(this.zoomInTicksEditBox, formatStageTicks(this.zoomInStageTicks));
            }
        }
        if (this.bodyHeightSlider != null) {
            LayoutRect rect = getItemRect(ITEM_BODY_HEIGHT_SLIDER);
            setWidgetRectangle(this.bodyHeightSlider, rect.width, rect.height, rect.x, rect.y);
            this.bodyHeightSlider.setValue(this.bodyCameraHeight);
        }
        if (this.bodyGlideSlider != null) {
            LayoutRect rect = getItemRect(ITEM_BODY_GLIDE_SLIDER);
            setWidgetRectangle(this.bodyGlideSlider, rect.width, rect.height, rect.x, rect.y);
            this.bodyGlideSlider.setValue(this.bodyGlideHeight);
        }
        if (this.bodyGlideTicksEditBox != null) {
            LayoutRect rect = getItemRect(ITEM_BODY_GLIDE_TICKS_FIELD);
            setWidgetRectangle(this.bodyGlideTicksEditBox, rect.width, rect.height, rect.x, rect.y);
            updateScaledEditBox(this.bodyGlideTicksEditBox);
            this.bodyGlideTicksEditBox.setEditable(!this.layoutDebugEnabled);
            if (!this.bodyGlideTicksEditBox.isFocused()) {
                setEditBoxValue(this.bodyGlideTicksEditBox, Integer.toString(this.bodyGlideTicks));
            }
        }
        if (this.playerHideTicksEditBox != null) {
            LayoutRect rect = getItemRect(ITEM_PLAYER_HIDE_TICKS_FIELD);
            setWidgetRectangle(this.playerHideTicksEditBox, rect.width, rect.height, rect.x, rect.y);
            updateScaledEditBox(this.playerHideTicksEditBox);
            this.playerHideTicksEditBox.setEditable(!this.layoutDebugEnabled);
            if (!this.playerHideTicksEditBox.isFocused()) {
                setEditBoxValue(this.playerHideTicksEditBox, Integer.toString(this.localPlayerHideTicks));
            }
        }
        if (this.prevPageButton != null) {
            LayoutRect rect = getItemRect(ITEM_PREV_PAGE_BUTTON);
            setWidgetRectangle(this.prevPageButton, rect.width, rect.height, rect.x, rect.y);
            this.prevPageButton.setMessage(Component.empty());
        }
        if (this.nextPageButton != null) {
            LayoutRect rect = getItemRect(ITEM_NEXT_PAGE_BUTTON);
            setWidgetRectangle(this.nextPageButton, rect.width, rect.height, rect.x, rect.y);
            this.nextPageButton.setMessage(Component.empty());
        }
        repositionPageTabs();

        LayoutRect panel = getContentRect();
        if (this.layoutDebugButton != null) {
            int buttonWidth = 90;
            setWidgetRectangle(this.layoutDebugButton, buttonWidth, 20, Math.max(6, this.width - buttonWidth - 6), 6);
            this.layoutDebugButton.setMessage(getLayoutDebugLabel());
        }
        ToolbarCursor cursor = new ToolbarCursor(DEBUG_TOOLBAR_MARGIN, DEBUG_TOOLBAR_MARGIN);
        int rightLimit = this.layoutDebugButton == null ? this.width - DEBUG_TOOLBAR_MARGIN : this.layoutDebugButton.getX() - DEBUG_TOOLBAR_GAP;
        if (this.aspectButton != null) {
            cursor = placeToolbarButton(this.aspectButton, getAspectLabel(), cursor, 82, rightLimit);
        }
        if (this.resetLayoutButton != null) {
            cursor = placeToolbarButton(this.resetLayoutButton, Component.translatable("gtalike_teleport.config.layout_reset"), cursor, 86, rightLimit);
        }
        if (this.resetItemSizeButton != null) {
            cursor = placeToolbarButton(this.resetItemSizeButton, Component.translatable("gtalike_teleport.config.layout_size_reset"), cursor, 88, rightLimit);
        }
        if (this.gridButton != null) {
            cursor = placeToolbarButton(this.gridButton, getGridLabel(), cursor, 78, rightLimit);
        }
        if (this.snapButton != null) {
            cursor = placeToolbarButton(this.snapButton, getSnapLabel(), cursor, 78, rightLimit);
        }
        repositionLayoutValueEditor(cursor, rightLimit);
        if (this.resetButton != null) {
            LayoutRect rect = getItemRect(ITEM_RESET_BUTTON);
            setWidgetRectangle(this.resetButton, rect.width, rect.height, rect.x, rect.y);
        }
        if (this.doneButton != null) {
            LayoutRect rect = getItemRect(ITEM_DONE_BUTTON);
            setWidgetRectangle(this.doneButton, rect.width, rect.height, rect.x, rect.y);
        }
        repositionTextEditor();
    }

    private void repositionPageTabs() {
        if (this.pageTabButtons == null || this.pageTabButtons.length == 0) {
            return;
        }
        ConfigPage[] pages = ConfigPage.values();
        for (int i = 0; i < this.pageTabButtons.length && i < pages.length; i++) {
            Button button = this.pageTabButtons[i];
            if (button == null) {
                continue;
            }
            LayoutRect rect = getItemRect(pages[i].tabItem);
            setWidgetRectangle(button, rect.width, rect.height, rect.x, rect.y);
            button.setMessage(Component.empty());
            button.active = pages[i] != this.currentPage;
        }
    }

    private LayoutRect[] getPageTabRects() {
        ConfigPage[] pages = ConfigPage.values();
        LayoutRect[] rects = new LayoutRect[pages.length];
        LayoutRect outer = toOuterRect(getContentRect());
        int totalWidth = Math.max(pages.length, outer.width);
        double scale = getContentScale();
        int tabHeight = Math.max(16, (int) Math.round(20 * scale));
        int y = Math.max(2, outer.y - tabHeight - Math.max(2, (int) Math.round(2 * scale)));
        int baseWidth = Math.max(24, totalWidth / pages.length);
        int remainder = Math.max(0, totalWidth - baseWidth * pages.length);
        int x = outer.x;
        for (int i = 0; i < pages.length; i++) {
            int width = baseWidth + (i < remainder ? 1 : 0);
            rects[i] = new LayoutRect(x, y, width, tabHeight);
            x += width;
        }
        return rects;
    }

    private LayoutRect getDefaultPageTabRect(String item) {
        ConfigPage[] pages = ConfigPage.values();
        LayoutRect[] rects = getPageTabRects();
        for (int i = 0; i < pages.length && i < rects.length; i++) {
            if (pages[i].tabItem.equals(item)) {
                return rects[i];
            }
        }
        return new LayoutRect(0, 0, 24, 20);
    }

    private boolean isPageTabItem(String item) {
        for (ConfigPage page : ConfigPage.values()) {
            if (page.tabItem.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private ToolbarCursor placeToolbarButton(Button button, Component label, ToolbarCursor cursor, int width, int rightLimit) {
        int clampedRightLimit = Math.max(width + DEBUG_TOOLBAR_MARGIN, rightLimit);
        int buttonX = cursor.x;
        int buttonY = cursor.y;
        if (buttonX + width > clampedRightLimit) {
            buttonX = DEBUG_TOOLBAR_MARGIN;
            buttonY += DEBUG_TOOLBAR_ROW_STEP;
        }
        setWidgetRectangle(button, width, DEBUG_TOOLBAR_BUTTON_HEIGHT, buttonX, buttonY);
        button.setMessage(label);
        return new ToolbarCursor(buttonX + width + DEBUG_TOOLBAR_GAP, buttonY);
    }
    private void beginLayoutEdit(String item, LayoutEditAction action, double mouseX, double mouseY) {
        this.selectedLayoutItem = item;
        this.editingLayoutItem = item;
        this.layoutEditAction = action;
        this.editStartRect = getEditableRect(item);
        this.editingRect = this.editStartRect;
        this.editStartMouseX = mouseX;
        this.editStartMouseY = mouseY;
        refreshTextEditor();
        refreshLayoutValueEditor();
    }

    private void updateLayoutEdit(double mouseX, double mouseY) {
        if (this.editStartRect == null || this.editingLayoutItem == null) {
            return;
        }

        int dx = (int) Math.round(mouseX - this.editStartMouseX);
        int dy = (int) Math.round(mouseY - this.editStartMouseY);
        int x = this.editStartRect.x;
        int y = this.editStartRect.y;
        int itemWidth = this.editStartRect.width;
        int itemHeight = this.editStartRect.height;

        if (this.layoutEditAction == LayoutEditAction.MOVE) {
            x += dx;
            y += dy;
        } else if (this.layoutEditAction == LayoutEditAction.RESIZE_RIGHT) {
            itemWidth += dx;
        } else if (this.layoutEditAction == LayoutEditAction.RESIZE_BOTTOM) {
            itemHeight += dy;
        } else if (this.layoutEditAction == LayoutEditAction.RESIZE_CORNER) {
            itemWidth += dx;
            itemHeight += dy;
            if (this.layoutAspectLocked && this.editStartRect.height > 0) {
                double aspect = this.editStartRect.width / (double) this.editStartRect.height;
                if (Math.abs(dx) >= Math.abs(dy)) {
                    itemHeight = (int) Math.round(itemWidth / aspect);
                } else {
                    itemWidth = (int) Math.round(itemHeight * aspect);
                }
            }
        }

        LayoutRect raw = new LayoutRect(x, y, itemWidth, itemHeight);
        LayoutRect constrained = ITEM_PANEL.equals(this.editingLayoutItem)
                ? constrainContentRect(raw)
                : constrainItemRect(this.editingLayoutItem, raw);
        this.editingRect = applyLayoutSnap(this.editingLayoutItem, constrained);
        repositionWidgets();
        refreshLayoutValueEditor();
    }

    private void finishLayoutEdit() {
        if (this.editingLayoutItem != null) {
            saveLayoutRect(this.editingLayoutItem, getEditableRect(this.editingLayoutItem));
        }
        this.layoutEditAction = LayoutEditAction.NONE;
        this.editingLayoutItem = null;
        this.editStartRect = null;
        this.editingRect = null;
        repositionWidgets();
        refreshLayoutValueEditor();
    }

    private LayoutRect applyLayoutSnap(String item, LayoutRect rect) {
        if (!this.layoutSnapEnabled || item == null || this.layoutEditAction == LayoutEditAction.NONE) {
            return rect;
        }

        List<Integer> horizontalTargets = getHorizontalSnapTargets(item);
        List<Integer> verticalTargets = getVerticalSnapTargets(item);
        LayoutRect snapped = rect;
        if (this.layoutEditAction == LayoutEditAction.MOVE) {
            int offsetX = findSnapOffset(new int[]{rect.x, rect.centerX(), rect.right()}, horizontalTargets);
            int offsetY = findSnapOffset(new int[]{rect.y, rect.centerY(), rect.bottom()}, verticalTargets);
            snapped = new LayoutRect(rect.x + offsetX, rect.y + offsetY, rect.width, rect.height);
        } else {
            int x = rect.x;
            int y = rect.y;
            int itemWidth = rect.width;
            int itemHeight = rect.height;
            if (this.layoutEditAction == LayoutEditAction.RESIZE_RIGHT || this.layoutEditAction == LayoutEditAction.RESIZE_CORNER) {
                int rightOffset = findSnapOffset(new int[]{rect.right()}, horizontalTargets);
                int widthOffset = findSnapOffset(new int[]{rect.width}, getWidthSnapTargets(item));
                itemWidth = rect.width + chooseSnapOffset(rightOffset, widthOffset);
            }
            if (this.layoutEditAction == LayoutEditAction.RESIZE_BOTTOM || this.layoutEditAction == LayoutEditAction.RESIZE_CORNER) {
                int bottomOffset = findSnapOffset(new int[]{rect.bottom()}, verticalTargets);
                int heightOffset = findSnapOffset(new int[]{rect.height}, getHeightSnapTargets(item));
                itemHeight = rect.height + chooseSnapOffset(bottomOffset, heightOffset);
            }
            snapped = new LayoutRect(x, y, itemWidth, itemHeight);
        }
        return ITEM_PANEL.equals(item) ? constrainContentRect(snapped) : constrainItemRect(item, snapped);
    }

    private int chooseSnapOffset(int edgeOffset, int sizeOffset) {
        if (edgeOffset == 0) {
            return sizeOffset;
        }
        if (sizeOffset == 0) {
            return edgeOffset;
        }
        return Math.abs(sizeOffset) < Math.abs(edgeOffset) ? sizeOffset : edgeOffset;
    }

    private List<Integer> getWidthSnapTargets(String item) {
        List<Integer> targets = new ArrayList<>();
        LayoutRect panel = getContentRect();
        targets.add(panel.width);
        targets.add(Math.max(1, panel.width / 2));
        for (String visibleItem : getVisibleLayoutItems()) {
            if (!visibleItem.equals(item) && !ITEM_PANEL.equals(visibleItem)) {
                LayoutRect rect = getSelectableRect(visibleItem);
                targets.add(rect.width);
            }
        }
        return targets;
    }

    private List<Integer> getHeightSnapTargets(String item) {
        List<Integer> targets = new ArrayList<>();
        LayoutRect panel = getContentRect();
        targets.add(panel.height);
        targets.add(Math.max(1, panel.height / 2));
        for (String visibleItem : getVisibleLayoutItems()) {
            if (!visibleItem.equals(item) && !ITEM_PANEL.equals(visibleItem)) {
                LayoutRect rect = getSelectableRect(visibleItem);
                targets.add(rect.height);
            }
        }
        return targets;
    }
    private List<Integer> getHorizontalSnapTargets(String item) {
        List<Integer> targets = new ArrayList<>();
        targets.add(0);
        targets.add(this.width / 2);
        targets.add(this.width);
        if (!ITEM_PANEL.equals(item)) {
            LayoutRect panel = getContentRect();
            addHorizontalTargets(targets, panel);
            for (String visibleItem : getVisibleLayoutItems()) {
                if (!ITEM_PANEL.equals(visibleItem) && !visibleItem.equals(item)) {
                    addHorizontalTargets(targets, getSelectableRect(visibleItem));
                }
            }
        }
        return targets;
    }

    private List<Integer> getVerticalSnapTargets(String item) {
        List<Integer> targets = new ArrayList<>();
        targets.add(0);
        targets.add(this.height / 2);
        targets.add(this.height);
        if (!ITEM_PANEL.equals(item)) {
            LayoutRect panel = getContentRect();
            addVerticalTargets(targets, panel);
            for (String visibleItem : getVisibleLayoutItems()) {
                if (!ITEM_PANEL.equals(visibleItem) && !visibleItem.equals(item)) {
                    addVerticalTargets(targets, getSelectableRect(visibleItem));
                }
            }
        }
        return targets;
    }

    private static void addHorizontalTargets(List<Integer> targets, LayoutRect rect) {
        targets.add(rect.x);
        targets.add(rect.centerX());
        targets.add(rect.right());
    }

    private static void addVerticalTargets(List<Integer> targets, LayoutRect rect) {
        targets.add(rect.y);
        targets.add(rect.centerY());
        targets.add(rect.bottom());
    }

    private int findSnapOffset(int[] anchors, List<Integer> targets) {
        int bestDistance = SNAP_DISTANCE + 1;
        int bestOffset = 0;
        for (int anchor : anchors) {
            for (int target : targets) {
                int distance = Math.abs(target - anchor);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestOffset = target - anchor;
                }
            }
            if (this.layoutGridEnabled) {
                int target = roundToGrid(anchor, GRID_SIZE);
                int distance = Math.abs(target - anchor);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestOffset = target - anchor;
                }
            }
        }
        return bestDistance <= SNAP_DISTANCE ? bestOffset : 0;
    }

    private static int roundToGrid(int value, int grid) {
        return Math.round(value / (float) grid) * grid;
    }
    private LayoutEditAction getResizeAction(String item, double mouseX, double mouseY) {
        if (item == null || !isVisibleLayoutItem(item)) {
            return LayoutEditAction.NONE;
        }

        LayoutRect rect = getSelectableRect(item);
        boolean nearRight = Math.abs(mouseX - rect.right()) <= RESIZE_HIT_SIZE && mouseY >= rect.y && mouseY <= rect.bottom();
        boolean nearBottom = Math.abs(mouseY - rect.bottom()) <= RESIZE_HIT_SIZE && mouseX >= rect.x && mouseX <= rect.right();
        boolean nearCorner = mouseX >= rect.right() - RESIZE_HIT_SIZE * 2
                && mouseX <= rect.right() + RESIZE_HIT_SIZE
                && mouseY >= rect.bottom() - RESIZE_HIT_SIZE * 2
                && mouseY <= rect.bottom() + RESIZE_HIT_SIZE;

        if (nearCorner) {
            return LayoutEditAction.RESIZE_CORNER;
        }
        if (nearRight) {
            return LayoutEditAction.RESIZE_RIGHT;
        }
        if (nearBottom) {
            return LayoutEditAction.RESIZE_BOTTOM;
        }
        return LayoutEditAction.NONE;
    }

    private void extractLayoutGuides(GuiGraphics context) {
        LayoutRect panel = getContentRect();
        int startX = panel.x + Math.floorMod(-panel.x, GRID_SIZE);
        for (int x = startX; x <= panel.right(); x += GRID_SIZE) {
            drawVerticalGuide(context, x, panel.y, panel.bottom(), DEBUG_GRID_COLOR);
        }

        int startY = panel.y + Math.floorMod(-panel.y, GRID_SIZE);
        for (int y = startY; y <= panel.bottom(); y += GRID_SIZE) {
            drawHorizontalGuide(context, y, panel.x, panel.right(), DEBUG_GRID_COLOR);
        }

        drawVerticalGuide(context, this.width / 2, 0, this.height, DEBUG_CENTER_GUIDE_COLOR);
        drawHorizontalGuide(context, this.height / 2, 0, this.width, DEBUG_CENTER_GUIDE_COLOR);
        drawVerticalGuide(context, panel.centerX(), panel.y, panel.bottom(), DEBUG_PANEL_GUIDE_COLOR);
        drawHorizontalGuide(context, panel.centerY(), panel.x, panel.right(), DEBUG_PANEL_GUIDE_COLOR);

        if (this.selectedLayoutItem != null && isVisibleLayoutItem(this.selectedLayoutItem)) {
            LayoutRect selected = getSelectableRect(this.selectedLayoutItem);
            drawVerticalGuide(context, selected.centerX(), panel.y, panel.bottom(), DEBUG_SELECTED_GUIDE_COLOR);
            drawHorizontalGuide(context, selected.centerY(), panel.x, panel.right(), DEBUG_SELECTED_GUIDE_COLOR);
        }
    }

    private static void drawVerticalGuide(GuiGraphics context, int x, int y1, int y2, int color) {
        context.fill(x, y1, x + 1, y2, color);
    }

    private static void drawHorizontalGuide(GuiGraphics context, int y, int x1, int x2, int color) {
        context.fill(x1, y, x2, y + 1, color);
    }
    private void extractLayoutDebugOverlays(GuiGraphics context) {
        for (String item : getVisibleLayoutItems()) {
            LayoutRect rect = getSelectableRect(item);
            boolean selected = item.equals(this.selectedLayoutItem);
            int color = selected ? DEBUG_BORDER_COLOR : DEBUG_ITEM_BORDER_COLOR;
            context.renderOutline(rect.x, rect.y, rect.width, rect.height, color);
        }

        if (this.selectedLayoutItem != null && isVisibleLayoutItem(this.selectedLayoutItem)) {
            LayoutRect rect = getSelectableRect(this.selectedLayoutItem);
            context.fill(rect.right() - 2, rect.y, rect.right() + 2, rect.bottom(), DEBUG_HANDLE_COLOR);
            context.fill(rect.x, rect.bottom() - 2, rect.right(), rect.bottom() + 2, DEBUG_HANDLE_COLOR);
            context.fill(rect.right() - 9, rect.bottom() - 9, rect.right() + 2, rect.bottom() + 2, 0xDDFFFF55);
            context.renderOutline(rect.right() - 9, rect.bottom() - 9, 11, 11, 0xFFFFFFFF);
        }
    }

    private String findLayoutItemAt(double mouseX, double mouseY) {
        List<String> items = getVisibleLayoutItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            String item = items.get(i);
            if (getSelectableRect(item).contains(mouseX, mouseY)) {
                return item;
            }
        }
        return null;
    }

    private void selectLayoutItem(String item) {
        if (item != null && !item.equals(this.selectedLayoutItem)) {
            this.selectedLayoutItem = item;
            refreshTextEditor();
            refreshLayoutValueEditor();
            repositionWidgets();
        }
    }

    private boolean isDebugControlAt(double mouseX, double mouseY) {
        return isWidgetAt(this.layoutDebugButton, mouseX, mouseY)
                || isWidgetAt(this.aspectButton, mouseX, mouseY)
                || isWidgetAt(this.resetLayoutButton, mouseX, mouseY)
                || isWidgetAt(this.resetItemSizeButton, mouseX, mouseY)
                || isWidgetAt(this.gridButton, mouseX, mouseY)
                || isWidgetAt(this.snapButton, mouseX, mouseY)
                || isWidgetAt(this.prevPageButton, mouseX, mouseY)
                || isWidgetAt(this.nextPageButton, mouseX, mouseY)
                || (this.layoutValueEditorVisible && isWidgetAt(this.layoutValueEditBox, mouseX, mouseY))
                || (this.layoutValueEditorVisible && isWidgetAt(this.applyLayoutValueButton, mouseX, mouseY))
                || (this.textEditorVisible && isWidgetAt(this.textEditBox, mouseX, mouseY));
    }

    private boolean isPageTabAt(double mouseX, double mouseY) {
        if (this.pageTabButtons == null) {
            return false;
        }
        for (Button button : this.pageTabButtons) {
            if (isWidgetAt(button, mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWidgetAt(AbstractWidget widget, double mouseX, double mouseY) {
        return widget != null && widget.isMouseOver(mouseX, mouseY);
    }

    private static void setWidgetRectangle(AbstractWidget widget, int width, int height, int x, int y) {
        if (widget == null) {
            return;
        }
        widget.setX(x);
        widget.setY(y);
        widget.setWidth(width);
        setWidgetHeight(widget, height);
    }

    private static void setWidgetHeight(AbstractWidget widget, int height) {
        try {
            java.lang.reflect.Field field = AbstractWidget.class.getDeclaredField("height");
            field.setAccessible(true);
            field.setInt(widget, height);
        } catch (ReflectiveOperationException ignored) {
        }
    }
    private void refreshLayoutValueEditor() {
        if (this.layoutValueEditBox == null) {
            return;
        }
        boolean visible = this.layoutDebugEnabled && this.selectedLayoutItem != null && isVisibleLayoutItem(this.selectedLayoutItem);
        this.layoutValueEditorVisible = visible;
        this.layoutValueEditBox.setVisible(visible);
        this.layoutValueEditBox.setEditable(visible);
        if (!visible) {
            this.layoutValueEditBox.setFocused(false);
            if (this.applyLayoutValueButton != null) {
                setWidgetRectangle(this.applyLayoutValueButton, 1, 1, -1000, -1000);
            }
            return;
        }
        this.updatingLayoutValueEditor = true;
        this.layoutValueEditBox.setValue(formatCurrentLayoutValues());
        this.updatingLayoutValueEditor = false;
    }

    private void repositionLayoutValueEditor(ToolbarCursor cursor, int rightLimit) {
        if (this.layoutValueEditBox == null) {
            return;
        }
        boolean visible = this.layoutDebugEnabled && this.selectedLayoutItem != null && isVisibleLayoutItem(this.selectedLayoutItem);
        this.layoutValueEditorVisible = visible;
        this.layoutValueEditBox.setVisible(visible);
        this.layoutValueEditBox.setEditable(visible);
        if (!visible) {
            if (this.applyLayoutValueButton != null) {
                setWidgetRectangle(this.applyLayoutValueButton, 1, 1, -1000, -1000);
            }
            return;
        }

        int editorX = DEBUG_TOOLBAR_MARGIN;
        int editorY = cursor.y + DEBUG_TOOLBAR_ROW_STEP;
        int applyWidth = 64;
        int availableWidth = Math.max(150, rightLimit - editorX);
        int editorWidth = Math.min(300, Math.max(140, availableWidth - applyWidth - DEBUG_TOOLBAR_GAP));
        if (editorX + editorWidth + DEBUG_TOOLBAR_GAP + applyWidth > rightLimit) {
            editorWidth = Math.min(300, Math.max(140, availableWidth));
            setWidgetRectangle(this.layoutValueEditBox, editorWidth, DEBUG_TOOLBAR_BUTTON_HEIGHT, editorX, editorY);
            if (this.applyLayoutValueButton != null) {
                setWidgetRectangle(this.applyLayoutValueButton, applyWidth, DEBUG_TOOLBAR_BUTTON_HEIGHT, editorX, editorY + DEBUG_TOOLBAR_ROW_STEP);
            }
            return;
        }

        setWidgetRectangle(this.layoutValueEditBox, editorWidth, DEBUG_TOOLBAR_BUTTON_HEIGHT, editorX, editorY);
        if (this.applyLayoutValueButton != null) {
            setWidgetRectangle(this.applyLayoutValueButton, applyWidth, DEBUG_TOOLBAR_BUTTON_HEIGHT, editorX + editorWidth + DEBUG_TOOLBAR_GAP, editorY);
        }
    }

    private String formatCurrentLayoutValues() {
        if (this.selectedLayoutItem == null || !isVisibleLayoutItem(this.selectedLayoutItem)) {
            return "x=0 y=0 w=0 h=0";
        }

        LayoutRect rect = getEditableRect(this.selectedLayoutItem);
        double x;
        double y;
        double w;
        double h;
        if (ITEM_PANEL.equals(this.selectedLayoutItem)) {
            x = this.width <= 0 ? 0.0D : rect.x / (double) this.width;
            y = this.height <= 0 ? 0.0D : rect.y / (double) this.height;
            w = this.width <= 0 ? 0.0D : rect.width / (double) this.width;
            h = this.height <= 0 ? 0.0D : rect.height / (double) this.height;
        } else {
            LayoutRect panel = getContentRect();
            x = panel.width <= 0 ? 0.0D : (rect.x - panel.x) / (double) panel.width;
            y = panel.height <= 0 ? 0.0D : (rect.y - panel.y) / (double) panel.height;
            w = panel.width <= 0 ? 0.0D : rect.width / (double) panel.width;
            h = panel.height <= 0 ? 0.0D : rect.height / (double) panel.height;
        }
        return String.format(Locale.ROOT, "x=%.3f y=%.3f w=%.3f h=%.3f", x, y, w, h);
    }
    private void refreshTextEditor() {
        if (this.textEditBox == null) {
            return;
        }
        boolean visible = this.layoutDebugEnabled && itemSupportsText(this.selectedLayoutItem) && isVisibleLayoutItem(this.selectedLayoutItem);
        this.textEditorVisible = visible;
        this.textEditBox.setVisible(visible);
        this.textEditBox.setEditable(visible);
        if (!visible) {
            this.textEditBox.setFocused(false);
            return;
        }
        this.updatingTextEditor = true;
        this.textEditBox.setValue(getItemText(this.selectedLayoutItem));
        this.updatingTextEditor = false;
        repositionTextEditor();
    }

    private void repositionTextEditor() {
        if (this.textEditBox == null) {
            return;
        }
        boolean visible = this.layoutDebugEnabled && itemSupportsText(this.selectedLayoutItem) && isVisibleLayoutItem(this.selectedLayoutItem);
        this.textEditorVisible = visible;
        this.textEditBox.setVisible(visible);
        this.textEditBox.setEditable(visible);
        if (!visible) {
            return;
        }

        LayoutRect anchor = getSelectableRect(this.selectedLayoutItem);
        int editorWidth = Math.min(360, Math.max(180, anchor.width + 80));
        int editorHeight = 20;
        int editorX = clamp(anchor.x, 8, Math.max(8, this.width - editorWidth - 8));
        int editorY = anchor.bottom() + 6;
        if (editorY + editorHeight > this.height - 8) {
            editorY = anchor.y - editorHeight - 6;
        }
        editorY = clamp(editorY, 8, Math.max(8, this.height - editorHeight - 8));
        setWidgetRectangle(this.textEditBox, editorWidth, editorHeight, editorX, editorY);
    }

    private void applyItemText(String item) {
        Component component = getItemComponent(item);
        if (ITEM_LINKED_SLIDER.equals(item) && this.linkedSlider != null) {
            this.linkedSlider.setMessage(component);
        } else if (ITEM_ZOOM_OUT_SLIDER.equals(item) && this.zoomOutSlider != null) {
            this.zoomOutSlider.setMessage(component);
        } else if (ITEM_ZOOM_IN_SLIDER.equals(item) && this.zoomInSlider != null) {
            this.zoomInSlider.setMessage(component);
        } else if (ITEM_ZOOM_STAGE_GLIDE_SLIDER.equals(item) && this.zoomStageGlideSlider != null) {
            this.zoomStageGlideSlider.setMessage(component);
        } else if (ITEM_BODY_HEIGHT_SLIDER.equals(item) && this.bodyHeightSlider != null) {
            this.bodyHeightSlider.setMessage(component);
        } else if (ITEM_BODY_GLIDE_SLIDER.equals(item) && this.bodyGlideSlider != null) {
            this.bodyGlideSlider.setMessage(component);
        } else if (ITEM_EFFECT_TOGGLE.equals(item) && this.effectToggleButton != null) {
            this.effectToggleButton.setMessage(Component.empty());
        } else if (ITEM_MOVEMENT_TOGGLE.equals(item) && this.movementToggleButton != null) {
            this.movementToggleButton.setMessage(Component.empty());
        } else if (ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item) && this.crossDimensionTravelToggleButton != null) {
            this.crossDimensionTravelToggleButton.setMessage(Component.empty());
        } else if (ITEM_SOUND_MODE_TOGGLE.equals(item) && this.soundModeToggleButton != null) {
            this.soundModeToggleButton.setMessage(Component.empty());
        } else if (ITEM_WARP_PLATE_TOGGLE.equals(item) && this.warpPlateToggleButton != null) {
            this.warpPlateToggleButton.setMessage(Component.empty());
        } else if (ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item) && this.externalTeleportToggleButton != null) {
            this.externalTeleportToggleButton.setMessage(Component.empty());
        } else if (ITEM_MINECRAFT_VOLUME_SLIDER.equals(item) && this.minecraftSoundVolumeSlider != null) {
            this.minecraftSoundVolumeSlider.setMessage(component);
        } else if (ITEM_CUSTOM_VOLUME_SLIDER.equals(item) && this.customSoundVolumeSlider != null) {
            this.customSoundVolumeSlider.setMessage(component);
        } else if (isPageTabItem(item)) {
            applyPageTabText(item);
        } else if (ITEM_PREV_PAGE_BUTTON.equals(item) && this.prevPageButton != null) {
            this.prevPageButton.setMessage(Component.empty());
        } else if (ITEM_NEXT_PAGE_BUTTON.equals(item) && this.nextPageButton != null) {
            this.nextPageButton.setMessage(Component.empty());
        } else if (ITEM_RESET_BUTTON.equals(item) && this.resetButton != null) {
            this.resetButton.setMessage(Component.empty());
        } else if (ITEM_DONE_BUTTON.equals(item) && this.doneButton != null) {
            this.doneButton.setMessage(Component.empty());
        }
    }

    private void applyPageTabText(String item) {
        if (this.pageTabButtons == null) {
            return;
        }
        ConfigPage[] pages = ConfigPage.values();
        for (int i = 0; i < pages.length && i < this.pageTabButtons.length; i++) {
            if (pages[i].tabItem.equals(item) && this.pageTabButtons[i] != null) {
                this.pageTabButtons[i].setMessage(Component.empty());
                return;
            }
        }
    }

    private boolean itemSupportsText(String item) {
        return isPageTabItem(item)
                || ITEM_TITLE.equals(item)
                || ITEM_DESCRIPTION.equals(item)
                || ITEM_GENERAL_TITLE.equals(item)
                || ITEM_GENERAL_DESCRIPTION.equals(item)
                || ITEM_STATUS_LINKED.equals(item)
                || ITEM_STATUS_UNLINKED.equals(item)
                || ITEM_EFFECT_LABEL.equals(item)
                || ITEM_MOVEMENT_LABEL.equals(item)
                || ITEM_CROSS_DIMENSION_TRAVEL_LABEL.equals(item)
                || ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item)
                || ITEM_ADVANCED1_TITLE.equals(item)
                || ITEM_ADVANCED1_DESCRIPTION.equals(item)
                || ITEM_ADVANCED2_TITLE.equals(item)
                || ITEM_ADVANCED2_DESCRIPTION.equals(item)
                || ITEM_ADVANCED3_TITLE.equals(item)
                || ITEM_ADVANCED3_DESCRIPTION.equals(item)
                || ITEM_SOUNDS_TITLE.equals(item)
                || ITEM_SOUNDS_DESCRIPTION.equals(item)
                || ITEM_SOUND_MODE_LABEL.equals(item)
                || ITEM_SOUND_MODE_TOGGLE.equals(item)
                || ITEM_OTHERS_TITLE.equals(item)
                || ITEM_OTHERS_DESCRIPTION.equals(item)
                || ITEM_WARP_PLATE_LABEL.equals(item)
                || ITEM_WARP_PLATE_TOGGLE.equals(item)
                || ITEM_EXTERNAL_TELEPORT_LABEL.equals(item)
                || ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item)
                || ITEM_MINECRAFT_VOLUME_SLIDER.equals(item)
                || ITEM_CUSTOM_VOLUME_SLIDER.equals(item)
                || ITEM_ZOOM_STAGE_GLIDE_TICKS_LABEL.equals(item)
                || ITEM_ZOOM_OUT_TICKS_LABEL.equals(item)
                || ITEM_ZOOM_IN_TICKS_LABEL.equals(item)
                || ITEM_BODY_GLIDE_TICKS_LABEL.equals(item)
                || ITEM_PLAYER_HIDE_LABEL.equals(item)
                || ITEM_LINKED_SLIDER.equals(item)
                || ITEM_ZOOM_OUT_SLIDER.equals(item)
                || ITEM_ZOOM_IN_SLIDER.equals(item)
                || ITEM_ZOOM_STAGE_GLIDE_SLIDER.equals(item)
                || ITEM_BODY_HEIGHT_SLIDER.equals(item)
                || ITEM_BODY_GLIDE_SLIDER.equals(item)
                || ITEM_RESET_BUTTON.equals(item)
                || ITEM_DONE_BUTTON.equals(item);
    }
    private boolean isManualTextItem(String item) {
        return ITEM_TITLE.equals(item)
                || ITEM_DESCRIPTION.equals(item)
                || ITEM_GENERAL_TITLE.equals(item)
                || ITEM_GENERAL_DESCRIPTION.equals(item)
                || ITEM_STATUS_LINKED.equals(item)
                || ITEM_STATUS_UNLINKED.equals(item)
                || ITEM_EFFECT_LABEL.equals(item)
                || ITEM_MOVEMENT_LABEL.equals(item)
                || ITEM_CROSS_DIMENSION_TRAVEL_LABEL.equals(item)
                || ITEM_ADVANCED1_TITLE.equals(item)
                || ITEM_ADVANCED1_DESCRIPTION.equals(item)
                || ITEM_ADVANCED2_TITLE.equals(item)
                || ITEM_ADVANCED2_DESCRIPTION.equals(item)
                || ITEM_ADVANCED3_TITLE.equals(item)
                || ITEM_ADVANCED3_DESCRIPTION.equals(item)
                || ITEM_SOUNDS_TITLE.equals(item)
                || ITEM_SOUNDS_DESCRIPTION.equals(item)
                || ITEM_SOUND_MODE_LABEL.equals(item)
                || ITEM_OTHERS_TITLE.equals(item)
                || ITEM_OTHERS_DESCRIPTION.equals(item)
                || ITEM_WARP_PLATE_LABEL.equals(item)
                || ITEM_EXTERNAL_TELEPORT_LABEL.equals(item)
                || ITEM_ZOOM_STAGE_GLIDE_TICKS_LABEL.equals(item)
                || ITEM_ZOOM_OUT_TICKS_LABEL.equals(item)
                || ITEM_ZOOM_IN_TICKS_LABEL.equals(item)
                || ITEM_BODY_GLIDE_TICKS_LABEL.equals(item)
                || ITEM_PLAYER_HIDE_LABEL.equals(item);
    }
    private boolean isCenteredTextItem(String item) {
        return ITEM_TITLE.equals(item)
                || ITEM_GENERAL_TITLE.equals(item)
                || ITEM_ADVANCED1_TITLE.equals(item)
                || ITEM_ADVANCED2_TITLE.equals(item)
                || ITEM_ADVANCED3_TITLE.equals(item)
                || ITEM_SOUNDS_TITLE.equals(item)
                || ITEM_OTHERS_TITLE.equals(item);
    }
    private boolean isMutedTextItem(String item) {
        return ITEM_DESCRIPTION.equals(item)
                || ITEM_GENERAL_DESCRIPTION.equals(item)
                || ITEM_ADVANCED1_DESCRIPTION.equals(item)
                || ITEM_ADVANCED2_DESCRIPTION.equals(item)
                || ITEM_ADVANCED3_DESCRIPTION.equals(item)
                || ITEM_SOUNDS_DESCRIPTION.equals(item)
                || ITEM_OTHERS_DESCRIPTION.equals(item)
                || (ITEM_EXTERNAL_TELEPORT_LABEL.equals(item) && !isExternalTeleportToggleAvailable())
                || ITEM_STATUS_UNLINKED.equals(item);
    }
    private boolean isManualButtonTextItem(String item) {
        return ITEM_EFFECT_TOGGLE.equals(item)
                || ITEM_MOVEMENT_TOGGLE.equals(item)
                || ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item)
                || ITEM_SOUND_MODE_TOGGLE.equals(item)
                || ITEM_WARP_PLATE_TOGGLE.equals(item)
                || ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item)
               
                || isPageTabItem(item)
                || ITEM_PREV_PAGE_BUTTON.equals(item)
                || ITEM_NEXT_PAGE_BUTTON.equals(item)
                || ITEM_RESET_BUTTON.equals(item)
                || ITEM_DONE_BUTTON.equals(item);
    }
    private void drawButtonText(GuiGraphics context, String item) {
        LayoutRect rect = getItemRect(item);
        Component text = getItemComponent(item);
        double scale = getButtonTextScale(rect, text);
        int textHeight = Math.max(1, (int) Math.round(this.font.lineHeight * scale));
        int y = rect.y + (rect.height - textHeight) / 2;
        drawScaledCenteredText(context, this.font, text, rect.x + rect.width / 2, y, TEXT_COLOR, scale);
    }

    private double getButtonTextScale(LayoutRect rect, Component text) {
        double scale = Math.max(0.25D, Math.min(4.0D, rect.height / 20.0D));
        int textWidth = this.font.width(text);
        int maxTextWidth = Math.max(1, rect.width - 8);
        if (textWidth > 0 && textWidth * scale > maxTextWidth) {
            scale = maxTextWidth / (double) textWidth;
        }
        return Math.max(0.25D, Math.min(4.0D, scale));
    }
    private Component getItemComponent(String item) {
        return Component.literal(getItemText(item));
    }

    private String getItemText(String item) {
        if (!itemSupportsText(item) || ITEM_DONE_BUTTON.equals(item)) {
            return getDefaultItemText(item);
        }
        return GtaLikeTeleportConfig.getConfigText(item, getDefaultItemText(item));
    }

    private String getDefaultItemText(String item) {
        if (ITEM_TAB_GENERAL.equals(item)) {
            return "General";
        }
        if (ITEM_TAB_ZOOM_STAGE.equals(item)) {
            return "Zoom Stage";
        }
        if (ITEM_TAB_ZOOM_STAGE_2.equals(item)) {
            return "Zoom Stage 2";
        }
        if (ITEM_TAB_SOUNDS.equals(item)) {
            return "Sounds";
        }
        if (ITEM_TAB_OTHERS.equals(item)) {
            return "Others";
        }
        if (ITEM_TITLE.equals(item)) {
            return Component.translatable("gtalike_teleport.config.title").getString();
        }
        if (ITEM_DESCRIPTION.equals(item)) {
            return Component.translatable("gtalike_teleport.config.description").getString();
        }
        if (ITEM_GENERAL_TITLE.equals(item)) {
            return "Grand Teleport General Settings";
        }
        if (ITEM_GENERAL_DESCRIPTION.equals(item)) {
            return "Adjust teleport behavior and input handling.";
        }
        if (ITEM_ADVANCED1_TITLE.equals(item)) {
            return "Grand Teleport Advanced 1";
        }
        if (ITEM_ADVANCED1_DESCRIPTION.equals(item)) {
            return "Tune extra glide used by zoom stages.";
        }
        if (ITEM_ADVANCED2_TITLE.equals(item)) {
            return "Grand Teleport Zoom Stage 2";
        }
        if (ITEM_ADVANCED2_DESCRIPTION.equals(item)) {
            return "Set tick lengths for each zoom stage.";
        }
        if (ITEM_ADVANCED3_TITLE.equals(item)) {
            return "Grand Teleport Advanced 3";
        }
        if (ITEM_ADVANCED3_DESCRIPTION.equals(item)) {
            return "Tune body camera height and player hiding.";
        }
        if (ITEM_SOUNDS_TITLE.equals(item)) {
            return "Grand Teleport Sound Settings";
        }
        if (ITEM_SOUNDS_DESCRIPTION.equals(item)) {
            return "Choose the teleport sound source and volume.";
        }
        if (ITEM_SOUND_MODE_LABEL.equals(item)) {
            return "Teleport sound source";
        }
        if (ITEM_SOUND_MODE_TOGGLE.equals(item)) {
            return this.customSoundsEnabled ? "Grand Teleport" : "Minecraft";
        }
        if (ITEM_MINECRAFT_VOLUME_SLIDER.equals(item)) {
            return "Minecraft sound volume";
        }
        if (ITEM_CUSTOM_VOLUME_SLIDER.equals(item)) {
            return "Custom sound volume";
        }
        if (ITEM_OTHERS_TITLE.equals(item)) {
            return "Grand Teleport Other Settings";
        }
        if (ITEM_OTHERS_DESCRIPTION.equals(item)) {
            return "Control third-party and server-triggered teleport effects.";
        }
        if (ITEM_WARP_PLATE_LABEL.equals(item)) {
            return "Waystones WarpPlate effect";
        }
        if (ITEM_WARP_PLATE_TOGGLE.equals(item)) {
            return this.warpPlateTransitionsEnabled ? "ON" : "OFF";
        }
        if (ITEM_EXTERNAL_TELEPORT_LABEL.equals(item)) {
            return "External teleport effect";
        }
        if (ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item)) {
            return isExternalTeleportToggleAvailable() ? (this.externalTeleportTransitionsEnabled ? "ON" : "OFF") : "SERVER OFF";
        }
        if (ITEM_STATUS_LINKED.equals(item)) {
            return Component.translatable("gtalike_teleport.config.linked").getString();
        }
        if (ITEM_STATUS_UNLINKED.equals(item)) {
            return Component.translatable("gtalike_teleport.config.unlinked").getString();
        }
        if (ITEM_EFFECT_LABEL.equals(item)) {
            return "Grand Teleport effect";
        }
        if (ITEM_MOVEMENT_LABEL.equals(item)) {
            return "Allow movement during teleport";
        }
        if (ITEM_CROSS_DIMENSION_TRAVEL_LABEL.equals(item)) {
            return "Cross-dimension camera travel";
        }
        if (ITEM_ZOOM_STAGE_GLIDE_SLIDER.equals(item)) {
            return "Zoom glide height";
        }
        if (ITEM_ZOOM_STAGE_GLIDE_TICKS_LABEL.equals(item)) {
            return "Zoom glide ticks";
        }
        if (ITEM_ZOOM_OUT_TICKS_LABEL.equals(item)) {
            return "Zoom-out stage ticks";
        }
        if (ITEM_ZOOM_IN_TICKS_LABEL.equals(item)) {
            return "Zoom-in stage ticks";
        }
        if (ITEM_BODY_HEIGHT_SLIDER.equals(item)) {
            return "Body camera height";
        }
        if (ITEM_BODY_GLIDE_SLIDER.equals(item)) {
            return "Body glide height";
        }
        if (ITEM_BODY_GLIDE_TICKS_LABEL.equals(item)) {
            return "Body glide ticks";
        }
        if (ITEM_PLAYER_HIDE_LABEL.equals(item)) {
            return "Hide player model";
        }
        if (ITEM_EFFECT_TOGGLE.equals(item)) {
            return this.effectEnabled ? "ON" : "OFF";
        }
        if (ITEM_MOVEMENT_TOGGLE.equals(item)) {
            return this.movementAllowed ? "ON" : "OFF";
        }
        if (ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item)) {
            return this.crossDimensionTravelEnabled ? "ON" : "OFF";
        }
        if (ITEM_PREV_PAGE_BUTTON.equals(item)) {
            return "<<";
        }
        if (ITEM_NEXT_PAGE_BUTTON.equals(item)) {
            return ">>";
        }
        if (ITEM_LINKED_SLIDER.equals(item)) {
            return Component.translatable("gtalike_teleport.config.zoom_heights").getString();
        }
        if (ITEM_ZOOM_OUT_SLIDER.equals(item)) {
            return Component.translatable("gtalike_teleport.config.zoom_out_heights").getString();
        }
        if (ITEM_ZOOM_IN_SLIDER.equals(item)) {
            return Component.translatable("gtalike_teleport.config.zoom_in_heights").getString();
        }
        if (ITEM_RESET_BUTTON.equals(item)) {
            return Component.translatable("gtalike_teleport.config.reset").getString();
        }
        if (ITEM_DONE_BUTTON.equals(item)) {
            return "Close";
        }
        return item;
    }

    private List<String> getVisibleLayoutItems() {
        List<String> items = new ArrayList<>();
        items.add(ITEM_PANEL);
        for (ConfigPage page : ConfigPage.values()) {
            items.add(page.tabItem);
        }
        if (this.currentPage == ConfigPage.ZOOM) {
            items.add(ITEM_TITLE);
            items.add(ITEM_DESCRIPTION);
            items.add(ITEM_PREV_PAGE_BUTTON);
            items.add(ITEM_NEXT_PAGE_BUTTON);
            items.add(ITEM_ZOOM_OUT_SLIDER);
            items.add(ITEM_ZOOM_IN_SLIDER);
            items.add(ITEM_DIMENSION_OVERWORLD);
            items.add(ITEM_DIMENSION_NETHER);
            items.add(ITEM_DIMENSION_END);
            items.add(ITEM_LINK_BUTTON);
        } else if (this.currentPage == ConfigPage.GENERAL) {
            items.add(ITEM_GENERAL_TITLE);
            items.add(ITEM_GENERAL_DESCRIPTION);
            items.add(ITEM_PREV_PAGE_BUTTON);
            items.add(ITEM_NEXT_PAGE_BUTTON);
            items.add(ITEM_EFFECT_LABEL);
            items.add(ITEM_EFFECT_TOGGLE);
            items.add(ITEM_MOVEMENT_LABEL);
            items.add(ITEM_MOVEMENT_TOGGLE);
            items.add(ITEM_CROSS_DIMENSION_TRAVEL_LABEL);
            items.add(ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE);
        } else if (this.currentPage == ConfigPage.ZOOM_STAGE_2) {
            items.add(ITEM_ADVANCED2_TITLE);
            items.add(ITEM_ADVANCED2_DESCRIPTION);
            items.add(ITEM_PREV_PAGE_BUTTON);
            items.add(ITEM_NEXT_PAGE_BUTTON);
            items.add(ITEM_ZOOM_OUT_TICKS_LABEL);
            items.add(ITEM_ZOOM_OUT_TICKS_FIELD);
            items.add(ITEM_ZOOM_IN_TICKS_LABEL);
            items.add(ITEM_ZOOM_IN_TICKS_FIELD);
        } else if (this.currentPage == ConfigPage.SOUNDS) {
            items.add(ITEM_SOUNDS_TITLE);
            items.add(ITEM_SOUNDS_DESCRIPTION);
            items.add(ITEM_PREV_PAGE_BUTTON);
            items.add(ITEM_NEXT_PAGE_BUTTON);
            items.add(ITEM_SOUND_MODE_LABEL);
            items.add(ITEM_SOUND_MODE_TOGGLE);
            items.add(ITEM_MINECRAFT_VOLUME_SLIDER);
            items.add(ITEM_CUSTOM_VOLUME_SLIDER);
        } else if (this.currentPage == ConfigPage.OTHERS) {
            items.add(ITEM_OTHERS_TITLE);
            items.add(ITEM_OTHERS_DESCRIPTION);
            items.add(ITEM_PREV_PAGE_BUTTON);
            items.add(ITEM_NEXT_PAGE_BUTTON);
            items.add(ITEM_WARP_PLATE_LABEL);
            items.add(ITEM_WARP_PLATE_TOGGLE);
            items.add(ITEM_EXTERNAL_TELEPORT_LABEL);
            items.add(ITEM_EXTERNAL_TELEPORT_TOGGLE);
        }
        items.add(ITEM_RESET_BUTTON);
        items.add(ITEM_DONE_BUTTON);
        return items;
    }
    private boolean isVisibleLayoutItem(String item) {
        return getVisibleLayoutItems().contains(item);
    }

    private String getStatusItemId() {
        return this.linked ? ITEM_STATUS_LINKED : ITEM_STATUS_UNLINKED;
    }

    private LayoutRect getSelectableRect(String item) {
        if (ITEM_PANEL.equals(item)) {
            return toOuterRect(getContentRect());
        }
        return getItemRect(item);
    }

    private LayoutRect getEditableRect(String item) {
        if (ITEM_PANEL.equals(item)) {
            return getContentRect();
        }
        return getItemRect(item);
    }

    private LayoutRect getItemRect(String item) {
        if (this.editingRect != null && item != null && item.equals(this.editingLayoutItem)) {
            return ITEM_PANEL.equals(item) ? getContentRect() : constrainItemRect(item, this.editingRect);
        }

        if (ITEM_PANEL.equals(item)) {
            return getContentRect();
        }

        LayoutRect panel = getContentRect();
        if (shouldUseCustomWidgetLayout(item)) {
            double[] layout = GtaLikeTeleportConfig.getConfigWidgetLayout(item);
            return constrainCustomItemRect(item, getScaledItemRect(panel, layout));
        }
        if (isDimensionButtonItem(item)) {
            LayoutRect anchored = getStatusAnchoredDimensionButtonRect(item, panel);
            if (anchored != null) {
                return constrainCustomItemRect(item, anchored);
            }
        }
        return getDefaultItemRect(item, panel);
    }

    private LayoutRect getScaledItemRect(LayoutRect panel, double[] layout) {
        int basePanelWidth = layout.length > 5 ? (int) Math.round(layout[4]) : 0;
        int basePanelHeight = layout.length > 5 ? (int) Math.round(layout[5]) : 0;
        if (basePanelWidth <= 0 || basePanelHeight <= 0) {
            LayoutRect storedBasePanel = getStoredBasePanelRect();
            if (storedBasePanel != null) {
                basePanelWidth = storedBasePanel.width;
                basePanelHeight = storedBasePanel.height;
            }
        }
        if (basePanelWidth > 0 && basePanelHeight > 0) {
            double scale = Math.min(panel.width / (double) basePanelWidth, panel.height / (double) basePanelHeight);
            int scaledBaseWidth = (int) Math.round(basePanelWidth * scale);
            int scaledBaseHeight = (int) Math.round(basePanelHeight * scale);
            int offsetX = panel.x + (panel.width - scaledBaseWidth) / 2;
            int offsetY = panel.y + (panel.height - scaledBaseHeight) / 2;
            return new LayoutRect(
                    offsetX + (int) Math.round(layout[0] * basePanelWidth * scale),
                    offsetY + (int) Math.round(layout[1] * basePanelHeight * scale),
                    (int) Math.round(layout[2] * basePanelWidth * scale),
                    (int) Math.round(layout[3] * basePanelHeight * scale)
            );
        }
        return new LayoutRect(
                panel.x + (int) Math.round(layout[0] * panel.width),
                panel.y + (int) Math.round(layout[1] * panel.height),
                (int) Math.round(layout[2] * panel.width),
                (int) Math.round(layout[3] * panel.height)
        );
    }

    private LayoutRect getStatusAnchoredDimensionButtonRect(String item, LayoutRect panel) {
        String statusItem = shouldUseCustomWidgetLayout(ITEM_STATUS_LINKED) ? ITEM_STATUS_LINKED : ITEM_STATUS_UNLINKED;
        if (!shouldUseCustomWidgetLayout(statusItem)) {
            return null;
        }
        LayoutRect statusRect = getScaledItemRect(panel, GtaLikeTeleportConfig.getConfigWidgetLayout(statusItem));
        int size = clamp(Math.max(18, statusRect.height + 8), 16, 28);
        int gap = Math.max(2, size / 5);
        int totalWidth = size * 3 + gap * 2;
        int index = dimensionButtonIndex(item);
        int x = statusRect.centerX() - totalWidth / 2 + index * (size + gap);
        int y = statusRect.centerY() - size / 2;
        return new LayoutRect(x, y, size, size);
    }

    private LayoutRect getDefaultDimensionButtonRect(String item, LayoutRect panel) {
        int size = 20;
        int gap = 4;
        int totalWidth = size * 3 + gap * 2;
        int x = panel.x + panel.width / 2 - totalWidth / 2 + dimensionButtonIndex(item) * (size + gap);
        int y = getBottomButtonY(panel) + 22;
        if (y + size > panel.bottom()) {
            y = getBottomButtonY(panel) - size - 6;
        }
        return new LayoutRect(x, y, size, size);
    }

    private static int dimensionButtonIndex(String item) {
        if (ITEM_DIMENSION_NETHER.equals(item)) {
            return 1;
        }
        if (ITEM_DIMENSION_END.equals(item)) {
            return 2;
        }
        return 0;
    }

    private static boolean isDimensionButtonItem(String item) {
        return ITEM_DIMENSION_OVERWORLD.equals(item)
                || ITEM_DIMENSION_NETHER.equals(item)
                || ITEM_DIMENSION_END.equals(item);
    }

    private LayoutRect getDefaultItemRect(String item, LayoutRect panel) {
        int sliderWidth = Math.max(160, panel.width - 48);
        int sliderY = getFirstSliderY(panel);
        if (isPageTabItem(item)) {
            return getDefaultPageTabRect(item);
        }
        if (isPageTitleItem(item)) {
            int textWidth = Math.max(64, this.font.width(getItemComponent(item)) + 10);
            return new LayoutRect(panel.x + (panel.width - textWidth) / 2, panel.y, textWidth, 10);
        }
        if (isPageDescriptionItem(item)) {
            int textWidth = Math.max(120, this.font.width(getItemComponent(item)) + 4);
            return new LayoutRect(panel.x + Math.max(0, (panel.width - textWidth) / 2), panel.y + 24, Math.min(panel.width, textWidth), 10);
        }
        if (ITEM_PREV_PAGE_BUTTON.equals(item)) {
            double scale = getContentScale();
            int width = Math.max(24, (int) Math.round(34 * scale));
            int height = Math.max(16, (int) Math.round(20 * scale));
            return new LayoutRect(panel.x, panel.y - Math.max(2, height / 10), width, height);
        }
        if (ITEM_NEXT_PAGE_BUTTON.equals(item)) {
            double scale = getContentScale();
            int width = Math.max(24, (int) Math.round(34 * scale));
            int height = Math.max(16, (int) Math.round(20 * scale));
            return new LayoutRect(panel.x + panel.width - width, panel.y - Math.max(2, height / 10), width, height);
        }
        if (isDimensionButtonItem(item)) {
            return getDefaultDimensionButtonRect(item, panel);
        }
        if (ITEM_STATUS_LINKED.equals(item) || ITEM_STATUS_UNLINKED.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 156, sliderY + 20, 150, 10);
        }
        if (ITEM_LINKED_SLIDER.equals(item) || ITEM_ZOOM_OUT_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, sliderY, sliderWidth, StageHeightSlider.HEIGHT);
        }
        if (ITEM_ZOOM_IN_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, sliderY + 52, sliderWidth, StageHeightSlider.HEIGHT);
        }
        if (ITEM_EFFECT_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 58, Math.max(120, panel.width - 170), 12);
        }
        if (ITEM_EFFECT_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, panel.y + 52, 92, 20);
        }
        if (ITEM_MOVEMENT_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 92, Math.max(120, panel.width - 170), 12);
        }
        if (ITEM_MOVEMENT_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, panel.y + 86, 92, 20);
        }
        if (ITEM_CROSS_DIMENSION_TRAVEL_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 126, Math.max(120, panel.width - 170), 12);
        }
        if (ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, panel.y + 120, 92, 20);
        }
        if (ITEM_SOUND_MODE_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 58, Math.max(120, panel.width - 190), 12);
        }
        if (ITEM_SOUND_MODE_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 132, panel.y + 52, 112, 20);
        }
        if (ITEM_WARP_PLATE_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 58, Math.max(120, panel.width - 190), 12);
        }
        if (ITEM_WARP_PLATE_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 132, panel.y + 52, 112, 20);
        }
        if (ITEM_EXTERNAL_TELEPORT_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, panel.y + 92, Math.max(120, panel.width - 190), 12);
        }
        if (ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 132, panel.y + 86, 112, 20);
        }
        if (ITEM_MINECRAFT_VOLUME_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, panel.y + 82, sliderWidth, SingleValueSlider.HEIGHT);
        }
        if (ITEM_CUSTOM_VOLUME_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, panel.y + 134, sliderWidth, SingleValueSlider.HEIGHT);
        }
        if (ITEM_ZOOM_STAGE_GLIDE_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, sliderY, sliderWidth, SingleValueSlider.HEIGHT);
        }
        if (ITEM_ZOOM_STAGE_GLIDE_TICKS_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, sliderY + 58, Math.max(120, panel.width - 160), 12);
        }
        if (ITEM_ZOOM_STAGE_GLIDE_TICKS_FIELD.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, sliderY + 52, 92, 20);
        }
        if (ITEM_ZOOM_OUT_TICKS_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, sliderY + 14, Math.max(120, panel.width - 210), 12);
        }
        if (ITEM_ZOOM_OUT_TICKS_FIELD.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 170, sliderY + 8, 150, 20);
        }
        if (ITEM_ZOOM_IN_TICKS_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, sliderY + 58, Math.max(120, panel.width - 210), 12);
        }
        if (ITEM_ZOOM_IN_TICKS_FIELD.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 170, sliderY + 52, 150, 20);
        }
        if (ITEM_BODY_HEIGHT_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, sliderY, sliderWidth, SingleValueSlider.HEIGHT);
        }
        if (ITEM_BODY_GLIDE_SLIDER.equals(item)) {
            return new LayoutRect(panel.x, sliderY + 44, sliderWidth, SingleValueSlider.HEIGHT);
        }
        if (ITEM_BODY_GLIDE_TICKS_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, sliderY + 102, Math.max(120, panel.width - 160), 12);
        }
        if (ITEM_BODY_GLIDE_TICKS_FIELD.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, sliderY + 96, 92, 20);
        }
        if (ITEM_PLAYER_HIDE_LABEL.equals(item)) {
            return new LayoutRect(panel.x + 20, sliderY + 132, Math.max(120, panel.width - 160), 12);
        }
        if (ITEM_PLAYER_HIDE_TICKS_FIELD.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 112, sliderY + 126, 92, 20);
        }
        if (ITEM_LINK_BUTTON.equals(item)) {
            return new LayoutRect(panel.x + panel.width / 2 - 10, getBottomButtonY(panel), 20, 20);
        }
        if (ITEM_RESET_BUTTON.equals(item)) {
            return new LayoutRect(panel.x, getBottomButtonY(panel), 150, 20);
        }
        if (ITEM_DONE_BUTTON.equals(item)) {
            return new LayoutRect(panel.x + panel.width - 150, getBottomButtonY(panel), 150, 20);
        }
        return new LayoutRect(panel.x, panel.y, 20, 20);
    }

    private LayoutRect constrainItemRect(String item, LayoutRect rect) {
        LayoutRect panel = getContentRect();
        int minWidth = 8;
        int minHeight = 8;
        if (ITEM_LINK_BUTTON.equals(item) || isDimensionButtonItem(item)) {
            minWidth = 16;
            minHeight = 16;
        } else if (ITEM_PREV_PAGE_BUTTON.equals(item) || ITEM_NEXT_PAGE_BUTTON.equals(item) || isPageTabItem(item)) {
            minWidth = 24;
            minHeight = 16;
        } else if (ITEM_EFFECT_TOGGLE.equals(item) || ITEM_MOVEMENT_TOGGLE.equals(item) || ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item) || ITEM_SOUND_MODE_TOGGLE.equals(item) || ITEM_WARP_PLATE_TOGGLE.equals(item) || ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item)) {
            minWidth = 44;
            minHeight = 18;
        } else if (isTickFieldItem(item)) {
            minWidth = 34;
            minHeight = 16;
        } else if (isSingleValueSliderItem(item)) {
            minWidth = 100;
            minHeight = SingleValueSlider.HEIGHT;
        } else if (ITEM_RESET_BUTTON.equals(item) || ITEM_DONE_BUTTON.equals(item)) {
            minWidth = 60;
            minHeight = 18;
        } else if (ITEM_LINKED_SLIDER.equals(item) || ITEM_ZOOM_OUT_SLIDER.equals(item) || ITEM_ZOOM_IN_SLIDER.equals(item)) {
            minWidth = 120;
            minHeight = StageHeightSlider.HEIGHT;
        } else if (itemSupportsText(item)) {
            minWidth = 20;
            minHeight = 8;
        }
        int availableWidth = Math.max(4, this.width - 16);
        int availableHeight = Math.max(4, this.height - 16);
        int effectiveMinWidth = Math.min(minWidth, availableWidth);
        int effectiveMinHeight = Math.min(minHeight, availableHeight);
        int maxWidth = Math.max(effectiveMinWidth, availableWidth);
        int maxHeight = Math.max(effectiveMinHeight, availableHeight);
        int itemWidth = clamp(rect.width, effectiveMinWidth, maxWidth);
        int itemHeight = clamp(rect.height, effectiveMinHeight, maxHeight);
        if (ITEM_LINK_BUTTON.equals(item) || isDimensionButtonItem(item)) {
            int size = clamp(Math.min(itemWidth, itemHeight), effectiveMinWidth, Math.min(maxWidth, maxHeight));
            itemWidth = size;
            itemHeight = size;
        }
        int x = clamp(rect.x, 0, Math.max(0, this.width - itemWidth));
        int y = clamp(rect.y, 0, Math.max(0, this.height - itemHeight));
        if (!ITEM_PANEL.equals(item) && panel.width > 0 && panel.height > 0) {
            x = clamp(x, panel.x - panel.width, panel.x + panel.width * 2);
            y = clamp(y, panel.y - panel.height, panel.y + panel.height * 2);
        }
        return new LayoutRect(x, y, itemWidth, itemHeight);
    }

    private LayoutRect constrainCustomItemRect(String item, LayoutRect rect) {
        int minWidth = ITEM_LINK_BUTTON.equals(item) || isDimensionButtonItem(item) ? 4 : 1;
        int minHeight = ITEM_LINK_BUTTON.equals(item) || isDimensionButtonItem(item) ? 4 : 1;
        if (ITEM_PREV_PAGE_BUTTON.equals(item) || ITEM_NEXT_PAGE_BUTTON.equals(item) || isPageTabItem(item) || ITEM_EFFECT_TOGGLE.equals(item) || ITEM_MOVEMENT_TOGGLE.equals(item) || ITEM_CROSS_DIMENSION_TRAVEL_TOGGLE.equals(item) || ITEM_SOUND_MODE_TOGGLE.equals(item) || ITEM_WARP_PLATE_TOGGLE.equals(item) || ITEM_EXTERNAL_TELEPORT_TOGGLE.equals(item) || isTickFieldItem(item)) {
            minWidth = 4;
            minHeight = 4;
        }
        int maxWidth = Math.max(minWidth, Math.max(4, this.width - 16));
        int maxHeight = Math.max(minHeight, Math.max(4, this.height - 16));
        int itemWidth = clamp(rect.width, minWidth, maxWidth);
        int itemHeight = clamp(rect.height, minHeight, maxHeight);
        if (isSingleValueSliderItem(item)) {
            itemHeight = Math.max(minHeight, Math.min(SingleValueSlider.HEIGHT, maxHeight));
        } else if (ITEM_LINKED_SLIDER.equals(item) || ITEM_ZOOM_OUT_SLIDER.equals(item) || ITEM_ZOOM_IN_SLIDER.equals(item)) {
            itemHeight = Math.max(minHeight, Math.min(StageHeightSlider.HEIGHT, maxHeight));
        }
        if (ITEM_LINK_BUTTON.equals(item) || isDimensionButtonItem(item)) {
            int size = clamp(Math.min(itemWidth, itemHeight), minWidth, Math.min(maxWidth, maxHeight));
            itemWidth = size;
            itemHeight = size;
        }
        int x = clamp(rect.x, 0, Math.max(0, this.width - itemWidth));
        int y = clamp(rect.y, 0, Math.max(0, this.height - itemHeight));
        LayoutRect panel = getContentRect();
        if (panel.width > 0 && panel.height > 0) {
            x = clamp(x, panel.x - panel.width, panel.x + panel.width * 2);
            y = clamp(y, panel.y - panel.height, panel.y + panel.height * 2);
        }
        return new LayoutRect(x, y, itemWidth, itemHeight);
    }
    private boolean isPageTitleItem(String item) {
        return ITEM_TITLE.equals(item)
                || ITEM_GENERAL_TITLE.equals(item)
                || ITEM_ADVANCED1_TITLE.equals(item)
                || ITEM_ADVANCED2_TITLE.equals(item)
                || ITEM_ADVANCED3_TITLE.equals(item)
                || ITEM_SOUNDS_TITLE.equals(item)
                || ITEM_OTHERS_TITLE.equals(item);
    }

    private boolean isPageDescriptionItem(String item) {
        return ITEM_DESCRIPTION.equals(item)
                || ITEM_GENERAL_DESCRIPTION.equals(item)
                || ITEM_ADVANCED1_DESCRIPTION.equals(item)
                || ITEM_ADVANCED2_DESCRIPTION.equals(item)
                || ITEM_ADVANCED3_DESCRIPTION.equals(item)
                || ITEM_SOUNDS_DESCRIPTION.equals(item)
                || ITEM_OTHERS_DESCRIPTION.equals(item);
    }

    private boolean isTickFieldItem(String item) {
        return ITEM_ZOOM_STAGE_GLIDE_TICKS_FIELD.equals(item)
                || ITEM_ZOOM_OUT_TICKS_FIELD.equals(item)
                || ITEM_ZOOM_IN_TICKS_FIELD.equals(item)
                || ITEM_BODY_GLIDE_TICKS_FIELD.equals(item)
                || ITEM_PLAYER_HIDE_TICKS_FIELD.equals(item);
    }

    private boolean isSingleValueSliderItem(String item) {
        return ITEM_ZOOM_STAGE_GLIDE_SLIDER.equals(item)
                || ITEM_BODY_HEIGHT_SLIDER.equals(item)
                || ITEM_BODY_GLIDE_SLIDER.equals(item)
                || ITEM_MINECRAFT_VOLUME_SLIDER.equals(item)
                || ITEM_CUSTOM_VOLUME_SLIDER.equals(item);
    }

    private Component getLayoutDebugLabel() {
        return Component.translatable(this.layoutDebugEnabled
                ? "gtalike_teleport.config.layout_debug_on"
                : "gtalike_teleport.config.layout_debug_off");
    }

    private Component getAspectLabel() {
        return Component.translatable(this.layoutAspectLocked
                ? "gtalike_teleport.config.layout_aspect_locked"
                : "gtalike_teleport.config.layout_aspect_free");
    }

    private Component getGridLabel() {
        return Component.translatable(this.layoutGridEnabled
                ? "gtalike_teleport.config.layout_grid_on"
                : "gtalike_teleport.config.layout_grid_off");
    }

    private Component getSnapLabel() {
        return Component.translatable(this.layoutSnapEnabled
                ? "gtalike_teleport.config.layout_snap_on"
                : "gtalike_teleport.config.layout_snap_off");
    }

    private boolean shouldUseCustomConfigLayout() {
        return GtaLikeTeleportConfig.hasCustomConfigLayout();
    }

    private boolean shouldUseCustomWidgetLayout(String item) {
        return GtaLikeTeleportConfig.hasConfigWidgetLayout(item);
    }
    private double getContentScale() {
        LayoutRect basePanel = getStoredBasePanelRect();
        if (basePanel == null || basePanel.width <= 0 || basePanel.height <= 0) {
            return 1.0D;
        }
        LayoutRect panel = getContentRect();
        return Math.max(0.2D, Math.min(4.0D, Math.min(panel.width / (double) basePanel.width, panel.height / (double) basePanel.height)));
    }

    private LayoutRect getStoredBasePanelRect() {
        if (!shouldUseCustomConfigLayout()) {
            return null;
        }
        int baseWidth = GtaLikeTeleportConfig.getConfigLayoutBaseWidth();
        int baseHeight = GtaLikeTeleportConfig.getConfigLayoutBaseHeight();
        if (baseWidth <= 0 || baseHeight <= 0) {
            baseWidth = this.sessionLayoutBaseWidth;
            baseHeight = this.sessionLayoutBaseHeight;
        }
        if (baseWidth <= 0 || baseHeight <= 0) {
            return null;
        }
        double[] layout = GtaLikeTeleportConfig.getConfigLayout();
        return new LayoutRect(
                (int) Math.round(layout[0] * baseWidth),
                (int) Math.round(layout[1] * baseHeight),
                Math.max(1, (int) Math.round(layout[2] * baseWidth)),
                Math.max(1, (int) Math.round(layout[3] * baseHeight))
        );
    }

    private int getFirstSliderY(LayoutRect panel) {
        return panel.y + 42;
    }

    private int getBottomButtonY(LayoutRect panel) {
        return panel.y + panel.height - 34;
    }

    private LayoutRect getContentRect() {
        if (ITEM_PANEL.equals(this.editingLayoutItem) && this.editingRect != null) {
            return constrainContentRect(this.editingRect);
        }

        if (shouldUseCustomConfigLayout()) {
            double[] layout = GtaLikeTeleportConfig.getConfigLayout();
            int baseWidth = GtaLikeTeleportConfig.getConfigLayoutBaseWidth();
            int baseHeight = GtaLikeTeleportConfig.getConfigLayoutBaseHeight();
            if (baseWidth <= 0 || baseHeight <= 0) {
                baseWidth = this.sessionLayoutBaseWidth;
                baseHeight = this.sessionLayoutBaseHeight;
            }
            if (baseWidth > 0 && baseHeight > 0) {
                double scale = Math.min(this.width / (double) baseWidth, this.height / (double) baseHeight);
                int scaledBaseWidth = (int) Math.round(baseWidth * scale);
                int scaledBaseHeight = (int) Math.round(baseHeight * scale);
                int offsetX = (this.width - scaledBaseWidth) / 2;
                int offsetY = (this.height - scaledBaseHeight) / 2;
                return constrainScaledContentRect(new LayoutRect(
                        offsetX + (int) Math.round(layout[0] * baseWidth * scale),
                        offsetY + (int) Math.round(layout[1] * baseHeight * scale),
                        (int) Math.round(layout[2] * baseWidth * scale),
                        (int) Math.round(layout[3] * baseHeight * scale)
                ));
            }
            return constrainContentRect(new LayoutRect(
                    (int) Math.round(layout[0] * this.width),
                    (int) Math.round(layout[1] * this.height),
                    (int) Math.round(layout[2] * this.width),
                    (int) Math.round(layout[3] * this.height)
            ));
        }

        return getDefaultContentRect();
    }

    private LayoutRect getDefaultContentRect() {
        int availableWidth = getAvailableContentWidth();
        int minWidth = Math.min(MIN_PANEL_WIDTH, availableWidth);
        int targetWidth = (int) Math.round(this.width * DEFAULT_PANEL_WIDTH_RATIO);
        int panelWidth = clamp(targetWidth, minWidth, Math.min(680, availableWidth));
        int defaultPanelHeight = 260;
        double heightRatio = DEFAULT_UNLINKED_PANEL_HEIGHT_RATIO;
        int targetHeight = (int) Math.round(this.height * heightRatio);
        int panelHeight = Math.max(defaultPanelHeight, targetHeight);
        int x = (this.width - panelWidth) / 2;
        int y = Math.max(getContentTopMargin(), (this.height - panelHeight) / 2);
        return constrainContentRect(new LayoutRect(x, y, panelWidth, panelHeight));
    }

    private LayoutRect constrainContentRect(LayoutRect rect) {
        int preferredMinHeight = 170;
        int topMargin = getContentTopMargin();
        int availableWidth = getAvailableContentWidth();
        int availableHeight = Math.max(80, this.height - topMargin - MAX_PANEL_MARGIN);
        int minWidth = Math.min(MIN_PANEL_WIDTH, availableWidth);
        int minHeight = Math.min(preferredMinHeight, availableHeight);
        int panelWidth = clamp(rect.width, minWidth, availableWidth);
        int panelHeight = clamp(rect.height, minHeight, availableHeight);
        int x = clamp(rect.x, MAX_PANEL_MARGIN, Math.max(MAX_PANEL_MARGIN, this.width - MAX_PANEL_MARGIN - panelWidth));
        int y = clamp(rect.y, topMargin, Math.max(topMargin, this.height - MAX_PANEL_MARGIN - panelHeight));
        return new LayoutRect(x, y, panelWidth, panelHeight);
    }

    private LayoutRect constrainScaledContentRect(LayoutRect rect) {
        int topMargin = getContentTopMargin();
        int availableWidth = getAvailableContentWidth();
        int availableHeight = Math.max(80, this.height - topMargin - MAX_PANEL_MARGIN);
        int panelWidth = Math.max(1, rect.width);
        int panelHeight = Math.max(1, rect.height);
        double scale = Math.min(1.0D, Math.min(availableWidth / (double) panelWidth, availableHeight / (double) panelHeight));
        if (scale < 1.0D) {
            panelWidth = Math.max(1, (int) Math.round(panelWidth * scale));
            panelHeight = Math.max(1, (int) Math.round(panelHeight * scale));
        }
        int x = clamp(rect.x, MAX_PANEL_MARGIN, Math.max(MAX_PANEL_MARGIN, this.width - MAX_PANEL_MARGIN - panelWidth));
        int y = clamp(rect.y, topMargin, Math.max(topMargin, this.height - MAX_PANEL_MARGIN - panelHeight));
        return new LayoutRect(x, y, panelWidth, panelHeight);
    }

    private int getAvailableContentWidth() {
        int screenLimited = Math.max(120, this.width - MAX_PANEL_MARGIN * 2);
        int ratioLimited = Math.max(120, (int) Math.round(this.width * MAX_CONTENT_WIDTH_RATIO));
        return Math.min(screenLimited, ratioLimited);
    }

    private int getContentTopMargin() {
        return this.layoutDebugEnabled ? Math.min(DEBUG_PANEL_TOP_MARGIN, Math.max(MAX_PANEL_MARGIN, this.height / 4)) : MAX_PANEL_MARGIN;
    }

    private static LayoutRect toOuterRect(LayoutRect panel) {
        return new LayoutRect(
                panel.x - OUTER_PADDING_LEFT,
                panel.y - OUTER_PADDING_TOP,
                panel.width + OUTER_PADDING_LEFT + OUTER_PADDING_RIGHT,
                panel.height + OUTER_PADDING_TOP + OUTER_PADDING_BOTTOM
        );
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private enum ConfigPage {
        GENERAL(ITEM_TAB_GENERAL),
        ZOOM(ITEM_TAB_ZOOM_STAGE),
        ZOOM_STAGE_2(ITEM_TAB_ZOOM_STAGE_2),
        SOUNDS(ITEM_TAB_SOUNDS),
        OTHERS(ITEM_TAB_OTHERS);

        private final String tabItem;

        ConfigPage(String tabItem) {
            this.tabItem = tabItem;
        }
    }

    private enum LayoutEditAction {
        NONE,
        MOVE,
        RESIZE_RIGHT,
        RESIZE_BOTTOM,
        RESIZE_CORNER
    }

    private record ToolbarCursor(int x, int y) {
    }
    private record LayoutRect(int x, int y, int width, int height) {
        int right() {
            return this.x + this.width;
        }

        int bottom() {
            return this.y + this.height;
        }

        int centerX() {
            return this.x + this.width / 2;
        }

        int centerY() {
            return this.y + this.height / 2;
        }

        boolean contains(double px, double py) {
            return px >= this.x && px <= right() && py >= this.y && py <= bottom();
        }
    }

    private static final class ScaledEditBox extends EditBox {
        private final net.minecraft.client.gui.Font font;
        private double textScale;
        private boolean selectingText;
        private int selectionAnchor;

        ScaledEditBox(net.minecraft.client.gui.Font font, int x, int y, int width, int height, Component message, double textScale) {
            super(font, x, y, width, height, message);
            this.font = font;
            setTextScale(textScale);
        }

        void setTextScale(double textScale) {
            this.textScale = Math.max(0.25D, Math.min(4.0D, textScale));
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int right = x + width;
            int bottom = y + height;
            int borderColor = isFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
            int textColor = this.active ? TEXT_COLOR : MUTED_TEXT_COLOR;

            context.fill(x, y, right, bottom, 0xFF000000);
            context.renderOutline(x, y, width, height, borderColor);

            String rawValue = getValue();
            Component value = Component.literal(rawValue);
            double scale = getTextScale(value);
            int textHeight = getTextHeight(scale);
            int textX = getTextX(scale);
            int textY = getTextY(textHeight);
            drawSelection(context, rawValue, textX, textY, textHeight, scale);
            drawScaledText(context, this.font, value, textX, textY, textColor, scale);

            if (isFocused()) {
                int cursorPosition = Math.max(0, Math.min(getCursorPosition(), rawValue.length()));
                int cursorX = getCursorX(rawValue, cursorPosition, textX, scale);
                int cursorWidth = Math.max(1, (int) Math.round(scale));
                if (cursorX < right - 3) {
                    context.fill(cursorX, textY - 1, cursorX + cursorWidth, textY + textHeight + 1, textColor);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.visible || button != 0 || !isMouseOver(mouseX, mouseY)) {
                return false;
            }

            int cursor = getCursorPositionForMouse(mouseX);
            setFocused(true);
            setCursorPosition(cursor);
            setHighlightPos(cursor);
            this.selectionAnchor = cursor;
            this.selectingText = true;
            return true;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (!this.selectingText || button != 0) {
                return false;
            }

            int cursor = getCursorPositionForMouse(mouseX);
            setCursorPosition(cursor);
            setHighlightPos(this.selectionAnchor);
            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (!this.selectingText || button != 0) {
                return false;
            }

            this.selectingText = false;
            return true;
        }

        private double getTextScale(Component text) {
            double scale = Math.max(0.45D, Math.min(4.0D, this.textScale));
            int textWidth = this.font.width(text);
            int maxTextWidth = Math.max(1, getWidth() - 8);
            if (textWidth > 0 && textWidth * scale > maxTextWidth) {
                scale = maxTextWidth / (double) textWidth;
            }
            return Math.max(0.45D, Math.min(4.0D, scale));
        }

        private int getTextHeight(double scale) {
            return Math.max(1, (int) Math.round(this.font.lineHeight * scale));
        }

        private int getTextX(double scale) {
            return getX() + Math.max(4, (int) Math.round(4 * scale));
        }

        private int getTextY(int textHeight) {
            return getY() + (getHeight() - textHeight) / 2;
        }

        private int getCursorX(String value, int cursorPosition, int textX, double scale) {
            int cursor = Math.max(0, Math.min(cursorPosition, value.length()));
            return textX + (int) Math.round(this.font.width(value.substring(0, cursor)) * scale);
        }

        private int getCursorPositionForMouse(double mouseX) {
            String value = getValue();
            double scale = getTextScale(Component.literal(value));
            double localX = (mouseX - getTextX(scale)) / scale;
            if (localX <= 0.0D) {
                return 0;
            }

            for (int i = 1; i <= value.length(); i++) {
                int previous = this.font.width(value.substring(0, i - 1));
                int current = this.font.width(value.substring(0, i));
                if (localX < (previous + current) / 2.0D) {
                    return i - 1;
                }
            }
            return value.length();
        }

        private void drawSelection(GuiGraphics context, String value, int textX, int textY, int textHeight, double scale) {
            String selected = getHighlighted();
            if (selected == null || selected.isEmpty()) {
                return;
            }

            int cursor = Math.max(0, Math.min(getCursorPosition(), value.length()));
            int selectedLength = selected.length();
            int start = cursor - selectedLength;
            int end = cursor;
            if (!matchesSelection(value, selected, start, end)) {
                start = cursor;
                end = cursor + selectedLength;
            }
            if (!matchesSelection(value, selected, start, end)) {
                return;
            }

            int left = getCursorX(value, start, textX, scale);
            int right = getCursorX(value, end, textX, scale);
            context.fill(Math.min(left, right), textY - 1, Math.max(left, right), textY + textHeight + 1, 0xAA2F5BBA);
        }

        private boolean matchesSelection(String value, String selected, int start, int end) {
            return start >= 0 && end <= value.length() && start <= end && value.substring(start, end).equals(selected);
        }
    }

    private static final class LinkLockButton extends AbstractWidget {
        private final Runnable onPress;
        private boolean locked;

        LinkLockButton(int x, int y, int width, int height, Runnable onPress) {
            super(x, y, width, height, Component.empty());
            this.onPress = onPress;
        }

        void setLocked(boolean locked) {
            this.locked = locked;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int background = !this.active ? 0xAA303030 : isHoveredOrFocused() ? 0xCC555555 : 0xAA3A3A3A;
            int border = !this.active ? 0xFF777777 : 0xFFFFFFFF;
            int textColor = !this.active ? 0xFF777777 : 0xFFFFFFFF;
            context.fill(x, y, x + width, y + height, background);
            context.renderOutline(x, y, width, height, border);

            Component icon = Component.literal(this.locked ? "🔒" : "🔓");
            net.minecraft.client.gui.Font font = Minecraft.getInstance().font;
            int iconWidth = Math.max(1, font.width(icon));
            int iconHeight = Math.max(1, font.lineHeight);
            double scale = Math.min((width - 4) / (double) iconWidth, (height - 4) / (double) iconHeight);
            scale = Math.max(0.25D, Math.min(4.0D, scale));
            int textHeight = Math.max(1, (int) Math.round(iconHeight * scale));
            int textY = y + (height - textHeight) / 2;
            drawScaledCenteredText(context, font, icon, x + width / 2, textY, textColor, scale);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.visible || !isMouseOver(mouseX, mouseY)) {
                return false;
            }
            this.onPress.run();
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, getMessage());
        }
    }
    private static final class DimensionIconButton extends AbstractWidget {
        private final ItemStack icon;
        private final ResourceLocation texture;
        private final Runnable onPress;
        private boolean selected;

        DimensionIconButton(int x, int y, int width, int height, ItemStack icon, Component label, ResourceLocation texture, Runnable onPress) {
            super(x, y, width, height, label);
            this.icon = icon.copy();
            this.texture = texture;
            this.onPress = onPress;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
            int x = getX();
            int y = getY();
            int right = x + getWidth();
            int bottom = y + getHeight();
            int borderColor = this.selected ? 0xFFFFFFFF : isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF555555;
            context.fill(x, y, right, bottom, this.selected ? 0xAA202020 : 0xAA101010);
            context.renderOutline(x, y, getWidth(), getHeight(), borderColor);

            double scale = Math.max(0.5D, Math.min(4.0D, Math.min((getWidth() - 4) / 16.0D, (getHeight() - 4) / 16.0D)));
            int iconX = x + (int) Math.round((getWidth() - 16.0D * scale) / 2.0D);
            int iconY = y + (int) Math.round((getHeight() - 16.0D * scale) / 2.0D);
            if (!this.icon.isEmpty()) {
                context.pose().pushPose();
                context.pose().translate((float) iconX, (float) iconY, 0.0F);
                context.pose().scale((float) scale, (float) scale, 1.0F);
                context.renderItem(this.icon, 0, 0);
                context.pose().popPose();
            } else {
                drawTextureIcon(context, iconX, iconY, scale, this.texture);
            }

            if (!this.selected) {
                context.fill(x + 1, y + 1, Math.max(x + 1, right - 1), Math.max(y + 1, bottom - 1), 0x99000000);
            }
        }

        private static void drawTextureIcon(GuiGraphics context, int x, int y, double scale, ResourceLocation texture) {
            int size = Math.max(4, (int) Math.round(16.0D * scale));
            context.blit(texture, x, y, 0.0F, 0.0F, size, size, 16, 16);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.visible || !isMouseOver(mouseX, mouseY)) {
                return false;
            }
            this.onPress.run();
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, getMessage());
        }
    }

    private static final class SingleValueSlider extends AbstractWidget {
        private static final int HEIGHT = 44;
        private static final int FRAME_INSET_Y = 2;
        private static final int HANDLE_WIDTH = 7;
        private static final int TRACK_MARGIN = 14;
        private static final int TRACK_Y = 29;
        private static final int TRACK_HEIGHT = 3;

        private final Consumer<Double> onChanged;
        private final double min;
        private final double max;
        private final double step;
        private final boolean integerValue;
        private final String suffix;
        private double value;
        private boolean dragging;

        SingleValueSlider(int x, int y, int width, Component label, double value, double min, double max, double step, boolean integerValue, String suffix, Consumer<Double> onChanged) {
            super(x, y, width, HEIGHT, label);
            this.min = min;
            this.max = max;
            this.step = step;
            this.integerValue = integerValue;
            this.suffix = suffix;
            this.onChanged = onChanged;
            this.value = sanitize(value);
        }

        void setValue(double value) {
            this.value = sanitize(value);
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
            int x = getX();
            int y = getY();
            int right = x + getWidth();
            int bottom = y + getHeight();
            double scale = getVisualScale();
            int borderColor = !this.active ? 0xFF555555 : isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF777777;
            int textColor = this.active ? 0xFFFFFFFF : 0xFF888888;
            int valueColor = this.active ? 0xFFE0E0E0 : 0xFF777777;
            int trackColor = this.active ? 0xFF777777 : 0xFF4D4D4D;

            int frameY = getFrameY();
            int frameHeight = getFrameHeight();
            context.fill(x, frameY, right, frameY + frameHeight, this.active ? 0xAA151515 : 0x88101010);
            context.renderOutline(x, frameY, getWidth(), frameHeight, borderColor);
            Component formatted = Component.literal(formatValue());
            double textScale = fitTopTextScale(scale, getMessage(), formatted);
            drawScaledText(context, Minecraft.getInstance().font, getMessage(), x + scaled(8, textScale), y + scaled(7, textScale), textColor, textScale);
            int valueWidth = (int) Math.round(Minecraft.getInstance().font.width(formatted) * textScale);
            drawScaledText(context, Minecraft.getInstance().font, formatted, right - valueWidth - scaled(8, textScale), y + scaled(7, textScale), valueColor, textScale);

            int trackLeft = x + getTrackMargin();
            int trackRight = right - getTrackMargin();
            int trackY = y + getTrackY();
            int trackHeight = Math.max(1, scaled(TRACK_HEIGHT, scale));
            context.fill(trackLeft, trackY, trackRight, trackY + trackHeight, trackColor);

            int handleWidth = Math.max(3, scaled(HANDLE_WIDTH, scale));
            int handleX = valueToX(this.value);
            int handleLeft = handleX - handleWidth / 2;
            int handleRight = handleLeft + handleWidth;
            int handleColor = !this.active ? 0xFF777777 : this.dragging ? 0xFFFFFFFF : 0xFFCCCCCC;
            context.fill(handleLeft, trackY - scaled(8, scale), handleRight, trackY + scaled(12, scale), handleColor);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.visible || !isMouseOver(mouseX, mouseY)) {
                return false;
            }

            this.dragging = true;
            updateValueFromMouse(mouseX);
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (!this.dragging) {
                return false;
            }

            updateValueFromMouse(mouseX);
            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (!this.dragging) {
                return false;
            }

            this.dragging = false;
            return true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, Component.literal(getMessage().getString() + ": " + formatValue()));
        }

        private void updateValueFromMouse(double mouseX) {
            double nextValue = sanitize(xToValue(mouseX));
            if (Math.abs(nextValue - this.value) < 0.0001D) {
                return;
            }
            this.value = nextValue;
            this.onChanged.accept(this.value);
        }

        private int valueToX(double value) {
            double progress = (value - this.min) / Math.max(0.0001D, this.max - this.min);
            int trackLeft = getX() + getTrackMargin();
            int trackWidth = getWidth() - getTrackMargin() * 2;
            return trackLeft + (int) Math.round(clamp(progress, 0.0D, 1.0D) * trackWidth);
        }

        private double xToValue(double mouseX) {
            int trackLeft = getX() + getTrackMargin();
            int trackWidth = getWidth() - getTrackMargin() * 2;
            double progress = (mouseX - trackLeft) / Math.max(1.0D, trackWidth);
            return this.min + clamp(progress, 0.0D, 1.0D) * (this.max - this.min);
        }

        private double sanitize(double rawValue) {
            double clamped = clamp(rawValue, this.min, this.max);
            if (this.step > 0.0D) {
                clamped = Math.round(clamped / this.step) * this.step;
            }
            if (this.integerValue) {
                clamped = Math.round(clamped);
            }
            return clamp(clamped, this.min, this.max);
        }

        private String formatValue() {
            if (this.integerValue) {
                return Integer.toString((int) Math.round(this.value)) + this.suffix;
            }
            return String.format(Locale.ROOT, "%.1f%s", this.value, this.suffix);
        }

        private double getVisualScale() {
            return Math.max(0.45D, Math.min(4.0D, getHeight() / (double) HEIGHT));
        }

        
        private int getFrameY() {
            return getY() + Math.min(FRAME_INSET_Y, Math.max(0, (getHeight() - 1) / 2));
        }

        private int getFrameHeight() {
            int inset = Math.min(FRAME_INSET_Y, Math.max(0, (getHeight() - 1) / 2));
            return Math.max(1, getHeight() - inset * 2);
        }

        private double fitTopTextScale(double scale, Component label, Component value) {
            int labelWidth = Minecraft.getInstance().font.width(label);
            int valueWidth = Minecraft.getInstance().font.width(value);
            int rawWidth = labelWidth + valueWidth + 28;
            int availableWidth = Math.max(1, getWidth() - 16);
            if (rawWidth > 0 && rawWidth * scale > availableWidth) {
                scale = availableWidth / (double) rawWidth;
            }
            return Math.max(0.25D, Math.min(1.0D, scale));
        }
        private int getTrackMargin() {
            return scaled(TRACK_MARGIN, getVisualScale());
        }

        private int getTrackY() {
            int trackY = scaled(TRACK_Y, getVisualScale());
            int minY = Math.max(10, getHeight() / 2);
            int maxY = Math.max(minY, getHeight() - 8);
            return Math.max(minY, Math.min(maxY, trackY));
        }

        private static int scaled(int value, double scale) {
            return Math.max(1, (int) Math.round(value * scale));
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    private static final class StageHeightSlider extends AbstractWidget {
        private static final int HEIGHT = 44;
        private static final int FRAME_INSET_Y = 2;
        private static final int BASE_WIDTH = 368;
        private static final int HANDLE_WIDTH = 7;
        private static final int TRACK_MARGIN = 14;
        private static final int TRACK_Y = 29;
        private static final int TRACK_HEIGHT = 3;

        private final Consumer<double[]> onChanged;
        private double[] values;
        private int activeHandle = -1;

        StageHeightSlider(int x, int y, int width, Component label, double[] values, Consumer<double[]> onChanged) {
            super(x, y, width, HEIGHT, label);
            this.values = GtaLikeTeleportConfig.sanitizeStageHeights(values);
            this.onChanged = onChanged;
        }

        void setValues(double[] values) {
            this.values = GtaLikeTeleportConfig.sanitizeStageHeights(values);
            this.activeHandle = -1;
        }

        void setEditable(boolean editable) {
            this.active = editable;
            if (!editable) {
                this.activeHandle = -1;
            }
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickProgress) {
            int x = getX();
            int y = getY();
            int right = x + getWidth();
            int bottom = y + getHeight();
            double scale = getVisualScale();
            int borderColor = !this.active ? 0xFF555555 : isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF777777;
            int textColor = this.active ? 0xFFFFFFFF : 0xFF888888;
            int valueColor = this.active ? 0xFFE0E0E0 : 0xFF777777;
            int trackColor = this.active ? 0xFF777777 : 0xFF4D4D4D;

            int frameY = getFrameY();
            int frameHeight = getFrameHeight();
            context.fill(x, frameY, right, frameY + frameHeight, this.active ? 0xAA151515 : 0x88101010);
            context.renderOutline(x, frameY, getWidth(), frameHeight, borderColor);
            Component formatted = formatValues();
            double textScale = fitTopTextScale(scale, getMessage(), formatted);
            drawScaledText(context, Minecraft.getInstance().font, getMessage(), x + scaled(8, textScale), y + scaled(7, textScale), textColor, textScale);
            int valuesWidth = (int) Math.round(Minecraft.getInstance().font.width(formatted) * textScale);
            drawScaledText(context, Minecraft.getInstance().font, formatted, right - valuesWidth - scaled(8, textScale), y + scaled(7, textScale), valueColor, textScale);

            int trackLeft = x + getTrackMargin();
            int trackRight = right - getTrackMargin();
            int trackY = y + getTrackY();
            int trackHeight = Math.max(1, scaled(TRACK_HEIGHT, scale));
            context.fill(trackLeft, trackY, trackRight, trackY + trackHeight, trackColor);

            int handleWidth = Math.max(3, scaled(HANDLE_WIDTH, scale));
            int handleTop = scaled(8, scale);
            int handleBottom = scaled(12, scale);
            for (int i = 0; i < values.length; i++) {
                int handleX = valueToX(values[i]);
                int handleColor = !this.active ? 0xFF777777 : i == activeHandle ? 0xFFFFFFFF : 0xFFCCCCCC;
                int handleLeft = handleX - handleWidth / 2;
                int handleRight = handleLeft + handleWidth;
                context.fill(handleLeft, trackY - handleTop, handleRight, trackY + handleBottom, handleColor);
            }
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.visible || !isMouseOver(mouseX, mouseY)) {
                return false;
            }

            this.activeHandle = nearestHandle(mouseX);
            updateValueFromMouse(mouseX);
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (this.activeHandle < 0) {
                return false;
            }

            updateValueFromMouse(mouseX);
            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (this.activeHandle < 0) {
                return false;
            }

            this.activeHandle = -1;
            return true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, Component.literal(getMessage().getString() + ": " + formatValues().getString()));
        }

        private void updateValueFromMouse(double mouseX) {
            if (this.activeHandle < 0) {
                return;
            }

            double value = xToValue(mouseX);
            double min = this.activeHandle == 0
                    ? GtaLikeTeleportConfig.getMinStageHeight()
                    : this.values[this.activeHandle - 1] + GtaLikeTeleportConfig.getMinStageGap();
            double max = this.activeHandle == this.values.length - 1
                    ? GtaLikeTeleportConfig.getMaxStageHeight()
                    : this.values[this.activeHandle + 1] - GtaLikeTeleportConfig.getMinStageGap();
            this.values[this.activeHandle] = clamp(Math.rint(value), min, max);
            this.onChanged.accept(this.values.clone());
        }

        private int nearestHandle(double mouseX) {
            int nearest = 0;
            double nearestDistance = Double.MAX_VALUE;
            for (int i = 0; i < this.values.length; i++) {
                double distance = Math.abs(mouseX - valueToX(this.values[i]));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = i;
                }
            }
            return nearest;
        }

        private int valueToX(double value) {
            double min = GtaLikeTeleportConfig.getMinStageHeight();
            double max = GtaLikeTeleportConfig.getMaxStageHeight();
            double progress = (value - min) / Math.max(1.0D, max - min);
            int trackLeft = getX() + getTrackMargin();
            int trackWidth = getWidth() - getTrackMargin() * 2;
            return trackLeft + (int) Math.round(clamp(progress, 0.0D, 1.0D) * trackWidth);
        }

        private double xToValue(double mouseX) {
            int trackLeft = getX() + getTrackMargin();
            int trackWidth = getWidth() - getTrackMargin() * 2;
            double progress = (mouseX - trackLeft) / Math.max(1.0D, trackWidth);
            return GtaLikeTeleportConfig.getMinStageHeight()
                    + clamp(progress, 0.0D, 1.0D)
                    * (GtaLikeTeleportConfig.getMaxStageHeight() - GtaLikeTeleportConfig.getMinStageHeight());
        }

        private double getVisualScale() {
            double heightScale = getHeight() / (double) HEIGHT;
            double widthScale = getWidth() / (double) BASE_WIDTH;
            return Math.max(0.25D, Math.min(1.0D, Math.min(heightScale, widthScale)));
        }

        
        private int getFrameY() {
            return getY() + Math.min(FRAME_INSET_Y, Math.max(0, (getHeight() - 1) / 2));
        }

        private int getFrameHeight() {
            int inset = Math.min(FRAME_INSET_Y, Math.max(0, (getHeight() - 1) / 2));
            return Math.max(1, getHeight() - inset * 2);
        }

        private double fitTopTextScale(double scale, Component label, Component value) {
            int labelWidth = Minecraft.getInstance().font.width(label);
            int valueWidth = Minecraft.getInstance().font.width(value);
            int rawWidth = labelWidth + valueWidth + 28;
            int availableWidth = Math.max(1, getWidth() - 16);
            if (rawWidth > 0 && rawWidth * scale > availableWidth) {
                scale = availableWidth / (double) rawWidth;
            }
            return Math.max(0.25D, Math.min(1.0D, scale));
        }
        private int getTrackMargin() {
            return scaled(TRACK_MARGIN, getVisualScale());
        }

        private int getTrackY() {
            int trackY = scaled(TRACK_Y, getVisualScale());
            int min = Math.max(10, getHeight() / 2);
            int max = Math.max(min, getHeight() - 8);
            return Math.max(min, Math.min(max, trackY));
        }
        private static int scaled(int value, double scale) {
            return Math.max(1, (int) Math.round(value * scale));
        }

        private Component formatValues() {
            return Component.literal((int) values[0] + " / " + (int) values[1] + " / " + (int) values[2]);
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        @Override
        public String toString() {
            return "StageHeightSlider{" + Arrays.toString(this.values) + '}';
        }
    }
}

