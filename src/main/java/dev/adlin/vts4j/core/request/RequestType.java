package dev.adlin.vts4j.core.request;

/**
 * Types of Request
 */
public enum RequestType {
    API_STATE("APIStateRequest"),
    STATISTICS("StatisticsRequest"),
    VTS_FOLDER_INFO("VTSFolderInfoRequest"),
    CURRENT_MODEL("CurrentModelRequest"),
    AVAILABLE_MODELS("AvailableModelsRequest"),
    MODEL_LOAD("ModelLoadRequest"),
    MOVE_MODEL("MoveModelRequest"),
    HOTKEYS_IN_CURRENT_MODEL("HotkeysInCurrentModelRequest"),
    HOTKEY_TRIGGER("HotkeyTriggerRequest"),
    EXPRESSION_STATE("ExpressionStateRequest"),
    EXPRESSION_ACTIVATION("ExpressionActivationRequest"),
    ART_MESH_LIST("ArtMeshListRequest"),
    COLOR_TINT("ColorTintRequest"),
    SCENE_COLOR_OVERLAY_INFO("SceneColorOverlayInfoRequest"),
    FACE_FOUND("FaceFoundRequest"),
    INPUT_PARAMETER_LIST("InputParameterListRequest"),
    PARAMETER_VALUE("ParameterValueRequest"),
    LIVE2D_PARAMETER_LIST("Live2DParameterListRequest"),
    PARAMETER_CREATION("ParameterCreationRequest"),
    PARAMETER_DELETION("ParameterDeletionRequest"),
    INJECT_PARAMETER_DATA("InjectParameterDataRequest"),
    GET_CURRENT_MODEL_PHYSICS("GetCurrentModelPhysicsRequest"),
    SET_CURRENT_MODEL_PHYSICS("SetCurrentModelPhysicsRequest"),
    NDI_CONFIG("NDIConfigRequest"),
    ITEM_LIST("ItemListRequest"),
    ITEM_LOAD("ItemLoadRequest"),
    ITEM_UNLOAD("ItemUnloadRequest"),
    ITEM_ANIMATION_CONTROL("ItemAnimationControlRequest"),
    ITEM_MOVE("ItemMoveRequest"),
    ITEM_SORT("ItemSortRequest"),
    ART_MESH_SELECTION("ArtMeshSelectionRequest"),
    ITEM_PIN("ItemPinRequest"),
    POST_PROCESSING_LIST("PostProcessingListRequest"),
    POST_PROCESSING_UPDATE("PostProcessingUpdateRequest");


    private final String requestName;

    RequestType(String requestName) {
        this.requestName = requestName;
    }

    /**
     * @return Request name
     */
    public String getRequestName() {
        return requestName;
    }
}
