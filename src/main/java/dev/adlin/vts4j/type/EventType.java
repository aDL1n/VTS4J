package dev.adlin.vts4j.type;

import org.jetbrains.annotations.Nullable;

public enum EventType {
    TEST("TestEvent"),
    MODEL_LOADED("ModelLoadedEvent"),
    TRACKING_STATUS_CHANGED("TrackingStatusChangedEvent"),
    BACKGROUND_CHANGED("BackgroundChangedEvent"),
    MODEL_CONFIG_CHANGED("ModelConfigChangedEvent"),
    MODEL_MOVED("ModelMovedEvent"),
    MODEL_OUTLINE("ModelOutlineEvent"),
    HOTKEY_TRIGGERED("HotkeyTriggeredEvent"),
    MODEL_ANIMATION("ModelAnimationEvent"),
    ITEM("ItemEvent"),
    MODEL_CLICKED("ModelClickedEvent"),
    POST_PROCESSING("PostProcessingEvent"),
    LIVE2D_CUBISM_EDITOR_CONNECTED("Live2DCubismEditorConnectedEvent");


    private final String name;

    EventType(String eventName) {
        this.name = eventName;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static EventType valueOfName(String eventName) {
        for (EventType type : EventType.values())
            if (type.getName().equals(eventName)) return type;
        return null;
    }
}
