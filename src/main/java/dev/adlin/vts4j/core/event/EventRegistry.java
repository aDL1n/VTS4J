package dev.adlin.vts4j.core.event;

import dev.adlin.vts4j.core.event.impl.TestEvent;

import java.util.HashMap;
import java.util.Map;

public class EventRegistry {
    private static final Map<EventType, Class<? extends Event>> eventClasses = new HashMap<>();

    static {
         register(EventType.TEST, TestEvent.class);
    }

    private static void register(EventType type, Class<? extends Event> eventClass) {
        eventClasses.put(type, eventClass);
    }

    public static Class<? extends Event> getEventClass(EventType type) {
        return eventClasses.get(type);
    }
}