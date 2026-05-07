package dev.adlin.vts4j.hotkey;

import com.google.gson.annotations.SerializedName;

public enum HotkeyAction {
    @SerializedName("Unset")
    UNSET,

    @SerializedName("TriggerAnimation")
    TRIGGER_ANIMATION,

    @SerializedName("ChangeIdleAnimation")
    CHANGE_IDLE_ANIMATION,

    @SerializedName("ToggleExpression")
    TOGGLE_EXPRESSION,

    @SerializedName("RemoveAllExpressions")
    REMOVE_ALL_EXPRESSIONS,

    @SerializedName("MoveModel")
    MOVE_MODEL,

    @SerializedName("ChangeBackground")
    CHANGE_BACKGROUND,

    @SerializedName("ReloadMicrophone")
    RELOAD_MICROPHONE,

    @SerializedName("ReloadTextures")
    RELOAD_TEXTURES,

    @SerializedName("CalibrateCam")
    CALIBRATE_CAM,

    @SerializedName("ChangeVTSModel")
    CHANGE_VTS_MODEL,

    @SerializedName("TakeScreenshot")
    TAKE_SCREENSHOT,

    @SerializedName("ScreenColorOverlay")
    SCREEN_COLOR_OVERLAY,

    @SerializedName("RemoveAllItems")
    REMOVE_ALL_ITEMS,

    @SerializedName("ToggleItemScene")
    TOGGLE_ITEM_SCENE,

    @SerializedName("DownloadRandomWorkshopItem")
    DOWNLOAD_RANDOM_WORKSHOP_ITEM,

    @SerializedName("ExecuteItemAction")
    EXECUTE_ITEM_ACTION,

    @SerializedName("ArtMeshColorPreset")
    ART_MESH_COLOR_PRESET,

    @SerializedName("ToggleTracker")
    TOGGLE_TRACKER,

    @SerializedName("ToggleTwitchFeature")
    TOGGLE_TWITCH_FEATURE,

    @SerializedName("LoadEffectPreset")
    LOAD_EFFECT_PRESET,

    @SerializedName("ToggleLive2DEditorAPI")
    TOGGLE_LIVE2D_EDITOR_API,

    @SerializedName("WebItemAction")
    WEB_ITEM_ACTION,

    @SerializedName("ToggleModelSound")
    TOGGLE_MODEL_SOUND
}
