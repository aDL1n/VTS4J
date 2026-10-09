package dev.adlin.vts4j.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.adlin.vts4j.event.impl.*;
import dev.adlin.vts4j.request.RequestType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A registry that provides bidirectional mapping between unique event string identifiers
 * and their respective {@link Event} class types.
 * <p>
 * This registry is primarily used to resolve event classes during network serialization
 * and request dispatching.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventRegistry {

    /**
     * Bidirectional map holding event string identifiers paired with their corresponding classes.
     */
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

    /**
     * Internal helper method to register an event mapping into the bidirectional map.
     *
     * @param eventName  the unique string identifier of the event
     * @param eventClass the Java class type extending {@link Event}
     * @throws IllegalArgumentException if the mapping violates bidirectional uniqueness constraints
     */
    private static void register(final @NonNull String eventName, final @NonNull Class<? extends Event> eventClass) {
        eventClasses.put(eventName, eventClass);
    }

    /**
     * Resolves the {@link Event} class associated with the string representation of a request type.
     *
     * @param requestType the request type to resolve from
     * @return the associated {@link Event} class, or {@code null} if no mapping exists
     */
    public static @Nullable Class<? extends Event> getEventClass(final @NonNull RequestType requestType) {
        return eventClasses.get(requestType.toString());
    }

    /**
     * Resolves the {@link Event} class associated with the provided unique event name.
     *
     * @param eventName the string identifier of the event
     * @return the associated {@link Event} class, or {@code null} if no mapping exists
     */
    public static @Nullable Class<? extends Event> getEventClass(final @NonNull String eventName) {
        return eventClasses.get(eventName);
    }

    /**
     * Checks if the specified event class is registered.
     *
     * @param eventClass the event class to verify
     * @return {@code true} if the class is registered, {@code false} otherwise
     */
    public static boolean exists(final @NonNull Class<? extends Event> eventClass) {
        return eventClasses.containsValue(eventClass);
    }

    /**
     * Checks if the specified event name identifier is registered.
     *
     * @param eventName the string identifier to verify
     * @return {@code true} if the name is registered, {@code false} otherwise
     */
    public static boolean exists(final @NonNull String eventName) {
        return eventClasses.containsKey(eventName);
    }

    /**
     * Retrieves the unique string name identifier for the given event class.
     *
     * @param eventClass the registered event class
     * @return the string identifier associated with the class, or {@code null} if not found
     */
    public static @Nullable String getName(final @NonNull Class<? extends Event> eventClass) {
        return eventClasses.inverse().get(eventClass);
    }
}