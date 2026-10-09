package dev.adlin.vts4j.event;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Event handler (Event Bus) responsible for registering event listeners
 * and dispatching events to their respective subscribers based on priority.
 */
@Slf4j
@NoArgsConstructor
public class EventHandler {

    private final Map<Class<? extends Event>, List<ListenerContainer>> events = new ConcurrentHashMap<>();

    /**
     * Dispatches the specified event to all registered listeners subscribed to its type.
     * <p>
     * Listeners are invoked synchronously in descending order of their priority.
     * </p>
     *
     * @param event the event instance to call and pass to listeners
     */
    public void callEvent(final @NonNull Event event) {
        final List<ListenerContainer> containers = events.get(event.getClass());
        if (containers == null) return;

        log.trace(
                "Dispatching event '{}' to {} listener(s)",
                event.getClass().getSimpleName(),
                containers.size()
        );
        containers.forEach(container -> container.notifyListener(event));
    }

    /**
     * Scans the provided listener instance for valid event listener methods and registers them.
     *
     * @param listener the listener object containing methods annotated with {@link EventListener}
     */
    public void registerListener(final @NonNull Listener listener) {
        log.debug("Scanning and registering listener class '{}'", listener.getClass().getName());

        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(this::isListenerMethod)
                .forEach(method -> registerMethod(listener, method));
    }

    /**
     * Checks if a method qualifies as an event listener.
     *
     * @param method the method to inspect
     * @return true if the method is non-static, annotated with {@link EventListener}, and has a valid signature
     */
    private boolean isListenerMethod(final @NonNull Method method) {
        return method.isAnnotationPresent(EventListener.class) &&
                !Modifier.isStatic(method.getModifiers()) &&
                isValid(method);
    }

    /**
     * Validates the method signature for an event listener.
     *
     * @param method the method to validate
     * @return true if the method accepts exactly one parameter which is a subclass of {@link Event}
     */
    private boolean isValid(final @NonNull Method method) {
        return method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    /**
     * Processes a validated method, creates its invoker, and adds it to the event registry.
     *
     * @param listener the instance of the listener class
     * @param method   the reflective method to turn into a subscriber
     */
    @SuppressWarnings("unchecked")
    private void registerMethod(final @NonNull Listener listener, final @NonNull Method method) {
        final EventListener annotation = method.getAnnotation(EventListener.class);
        final Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];

        log.trace(
                "Found event listener method '{}' for event type '{}' with priority '{}'",
                method.getName(),
                eventType.getSimpleName(),
                annotation.priority()
        );

        final Consumer<Event> invoker = createInvoker(listener, method, eventType);
        registerContainer(new ListenerContainer(eventType, invoker, annotation.priority()));
    }

    /**
     * Wraps the reflective method invocation into a high-performance functional consumer.
     *
     * @param listener the instance of the listener class
     * @param method   the method to invoke via reflection
     * @param type     the class type of the handled event
     * @return a safe functional consumer that encapsulates reflective invocation and handles internal errors
     */
    private @NonNull Consumer<Event> createInvoker(
            final @NonNull Listener listener,
            final @NonNull Method method,
            final @NonNull Class<? extends Event> type
    ) {
        return event -> {
            try {
                method.invoke(listener, event);
            } catch (Exception exception) {
                log.error(
                        "An error occurred while handling event '{}' in method '{}#{}'",
                        type.getSimpleName(),
                        listener.getClass().getSimpleName(),
                        method.getName(),
                        exception
                );
            }
        };
    }

    /**
     * Atomically registers a listener container into the registry and sorts the subscribers by priority.
     *
     * @param container the listener container to register
     */
    private void registerContainer(final @NonNull ListenerContainer container) {
       events.compute(
                container.eventType(),
                (key, containers) -> {
                    final List<ListenerContainer> newContainers = containers == null
                            ? new ArrayList<>()
                            : new ArrayList<>(containers);

                    newContainers.add(container);
                    newContainers.sort(Comparator.comparingInt(
                            (ListenerContainer value) -> value.priority().getWeight()
                    ).reversed());

                    return Collections.unmodifiableList(newContainers);
                }
        );

        log.debug(
                "Registered subscriber for '{}' with priority '{}'",
                container.eventType.getSimpleName(),
                container.priority
        );
    }

    /**
     * Immutable data container holding information about a registered event subscriber.
     */
    private record ListenerContainer(
            @NonNull Class<? extends Event> eventType,
            @NonNull Consumer<Event> listener,
            @NonNull EventPriority priority
    ) {

        /**
         * Notifies the subscriber by passing the fired event.
         *
         * @param event the triggered event
         */
        public void notifyListener(final @NonNull Event event) {
            listener.accept(event);
        }
    }
}
