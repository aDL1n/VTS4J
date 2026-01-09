package dev.adlin.vts4j.core.event;

import dev.adlin.vts4j.core.event.impl.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EventRegistry {
    private static final Map<String, Class<? extends Event>> eventClasses = new HashMap<>();

    static {
         register("TestEvent", TestEvent.class);
         register("ModelLoadedEvent", ModelLoadedEvent.class);
         register("TrackingStatusChangedEvent", TrackingStatusChangedEvent.class);
         register("BackgroundChangedEvent", BackgroundChangedEvent.class);
         register("ModelConfigChangedEvent", ModelConfigChangedEvent.class);
         register("ModelMovedEvent", ModelMovedEvent.class);
         register("ModelOutlineEvent", ModelOutlineEvent.class);
         register("HotkeyTriggeredEvent", HotkeyTriggeredEvent.class);
         register("ModelAnimationEvent", ModelAnimationEvent.class);
         register("ItemEvent", ItemEvent.class);
         register("ModelClickedEvent", ModelClickedEvent.class);
         register("PostProcessingEvent", PostProcessingEvent.class);
         register("Live2DCubismEditorConnectedEvent", Live2DCubismEditorConnectedEvent.class);
    }

    private static void register(String eventName, Class<? extends Event> eventClass) {
        eventClasses.put(eventName, eventClass);
    }

    @Nullable
    public static Class<? extends Event> getEventClass(String eventName) {
        return eventClasses.get(eventName);
    }

    public static boolean exists(String eventName) {
        return eventClasses.containsKey(eventName);
    }
}