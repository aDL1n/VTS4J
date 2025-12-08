package dev.adlin.vts4j.type;

import org.jetbrains.annotations.Nullable;

/**
 * Types of events
 */
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

    /**
     * @return Name of EventType
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the EventType for the given event name.
     * This method searches all available EventType values ​​and returns the one
     * whose name matches the provided eventName.
     *
     * @param eventName The name of the event to search for.
     * @return EventType with the matching name, or null if no match is found.
     */
    @Nullable
    public static EventType valueOfName(String eventName) {
        for (EventType type : EventType.values())
            if (type.getName().equals(eventName)) return type;
        return null;
    }
}
