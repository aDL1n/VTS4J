package dev.adlin.vts4j.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.adlin.vts4j.event.impl.*;
import org.jetbrains.annotations.NotNull;
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

    private EventRegistry() {
    }

    private static void register(String eventName, Class<? extends Event> eventClass) {
        eventClasses.put(eventName, eventClass);
    }

    public static @Nullable Class<? extends Event> getEventClass(final @NotNull String eventName) {
        return eventClasses.get(eventName);
    }

    public static boolean exists(final @NotNull Class<? extends Event> eventClass) {
        return eventClasses.containsValue(eventClass);
    }

    public static boolean exists(final @NotNull String eventName) {
        return eventClasses.containsKey(eventName);
    }

    public static @Nullable String getName(final @NotNull Class<? extends Event> eventClass) {
        return eventClasses.inverse().get(eventClass);
    }
}