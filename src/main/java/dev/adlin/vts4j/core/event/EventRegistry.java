package dev.adlin.vts4j.core.event;

import dev.adlin.vts4j.core.event.data.TestEventData;
import dev.adlin.vts4j.core.event.impl.TestEvent;
import dev.adlin.vts4j.type.EventType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EventRegistry {
    private static final Map<EventType, Class<? extends EventData>> dataModels = new HashMap<>();
    private static final Map<EventType, Function<? extends EventData, ? extends Event<?>>> eventFactories = new HashMap<>();

    static {
         register(EventType.TEST, TestEventData.class, TestEvent::new);
    }

    private static <T extends EventData> void register(
            EventType type,
            Class<T> dataClass,
            Function<T, Event<T>> factory
    ) {
        dataModels.put(type, dataClass);
        eventFactories.put(type, factory);
    }

    public static Class<? extends EventData> getEventDataClass(EventType type) {
        return dataModels.get(type);
    }

    public static <T extends EventData> Event<T> createEvent(EventType type, T data) {
        Function<T, Event<T>> factory = (Function<T, Event<T>>) eventFactories.get(type);
        if (factory == null) return null;

        return factory.apply(data);
    }
}