package dev.adlin.vts4j.event;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

    private final Map<Class<? extends Event>, List<ListenerContainer>> events = new ConcurrentHashMap<>();

    public void callEvent(final @NotNull Event event) {
        final List<ListenerContainer> containers = events.get(event.getClass());
        if (containers == null) return;

        LOGGER.trace("Event called: {}", event.getClass().getSimpleName());
        containers.forEach(container -> container.notifyListener(event));
    }

    public void registerListener(final @NotNull Listener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(this::isListenerMethod)
                .forEach(method -> registerMethod(listener, method));
    }

    private boolean isListenerMethod(final @NotNull Method method) {
        return method.isAnnotationPresent(EventListener.class) &&
                !Modifier.isStatic(method.getModifiers()) &&
                isValid(method);
    }

    private boolean isValid(final @NotNull Method method) {
        return method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    @SuppressWarnings("unchecked")
    private void registerMethod(final @NotNull Listener listener, final @NotNull Method method) {
        final EventListener annotation = method.getAnnotation(EventListener.class);
        final Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];

        final Consumer<Event> invoker = createInvoker(listener, method, eventType);
        registerContainer(new ListenerContainer(eventType, invoker, annotation.priority()));
    }

    private @NotNull Consumer<Event> createInvoker(
            final @NotNull Listener listener,
            final @NotNull Method method,
            final @NotNull Class<? extends Event> type
    ) {
        return event -> {
            try {
                method.invoke(listener, event);
            } catch (Exception ex) {
                LOGGER.error("Failed to handle event {}: {}", type.getSimpleName(), ex.getMessage());
            }
        };
    }

    private void registerContainer(final @NotNull ListenerContainer container) {
        final List<ListenerContainer> containers = new ArrayList<>(
                events.getOrDefault(container.eventType, Collections.emptyList())
        );

        containers.add(container);
        containers.sort(Comparator.comparingInt(
                (ListenerContainer listenerContainer) -> listenerContainer.priority().getId()).reversed());

        events.put(container.eventType, Collections.unmodifiableList(containers));
    }

    private record ListenerContainer(
            @NotNull Class<? extends Event> eventType,
            @NotNull Consumer<Event> listener,
            @NotNull EventPriority priority
    ) {
        public void notifyListener(final @NotNull Event event) {
            listener.accept(event);
        }
    }
}
