package dev.adlin.vts4j.api.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.adlin.vts4j.api.event.impl.*;
import org.jetbrains.annotations.Nullable;

public class EventRegistry {
    private static final BiMap<String, Class<? extends Event>> eventClasses = HashBiMap.create();

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

    public static boolean exists(Class<? extends Event> eventClass) {
        return eventClasses.containsValue(eventClass);
    }

    public static boolean exists(String eventName) {
        return eventClasses.containsKey(eventName);
    }

    public static String getName(Class<? extends Event> eventClass) {
        return eventClasses.inverse().get(eventClass);
    }
}