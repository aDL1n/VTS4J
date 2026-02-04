package dev.adlin.vts4j.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public class EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

    private final Map<Class<? extends Event>, List<ListenerContainer>> events = new HashMap<>();

    public void callEvent(final @NotNull Event event) {
        LOGGER.trace("Event called: {}", event.getClass().getName());

        if (eventsRegisteredForType(event.getClass())) {
            final List<ListenerContainer> eventListeners = new ArrayList<>(this.events.get(event.getClass()));

            eventListeners.sort((l1, l2) -> {
                if (l1.priority().getId() < l2.priority().getId())
                    return 1;
                else if (l1.priority().getId() > l2.priority().getId())
                    return -1;

                return 0;
            });

            for (ListenerContainer container : eventListeners) {
                container.notifyListener(event);
            }
        }
    }

    public void registerListener(final @NotNull Listener listenerClass) {
        this.registerListenerMethods(this.getEventMethods(listenerClass));
    }

    private void registerListenerMethods(final @NotNull List<EventMethod> eventMethods) {
        LOGGER.trace("Registering listener methods");

        for (EventMethod eventMethod : eventMethods) {
            final Consumer<Event> listener = event -> {
                try {
                    eventMethod.method.invoke(eventMethod.parent, event);
                } catch (Exception e) {
                    LOGGER.error("Failed to register method of event: {}", e.getMessage());
                }
            };

            final ListenerContainer container = new ListenerContainer(
                    eventMethod.eventType,
                    listener,
                    eventMethod.priority
            );

            this.registerListenerContainer(container);
        }
    }

    private void registerListenerContainer(final @NotNull ListenerContainer container) {
        if (!this.eventsRegisteredForType(container.eventType)) {
            this.events.put(container.eventType, new ArrayList<>());
        }

        this.events.get(container.eventType).add(container);
    }

    private @NotNull List<EventMethod> getEventMethods(final @NotNull Listener listenerClass) {
        final List<EventMethod> eventMethods = new ArrayList<>();

        for (Method method : listenerClass.getClass().getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                EventMethod eventMethod = EventMethod.createFrom(new AnalyzedMethod(method, listenerClass));
                if (eventMethod != null && this.hasEventListenerAnnotation(eventMethod))
                    eventMethods.add(eventMethod);
            }
        }

        return eventMethods;
    }

    private boolean hasEventListenerAnnotation(final @NotNull EventMethod eventMethod) {
        for (Annotation annotation : eventMethod.annotations) {
            if (annotation instanceof EventListener) return true;
        }

        return false;
    }

    private boolean eventsRegisteredForType(final @NotNull Class<? extends Event> eventType) {
        return events.containsKey(eventType);
    }

    private record ListenerContainer(Class<? extends Event> eventType, Consumer<Event> listener, EventPriority priority) {
        public void notifyListener(Event event) {
            listener.accept(event);
        }
    }

    private static class AnalyzedMethod {
        protected Method method;
        protected Listener parent;
        protected List<Annotation> annotations = new ArrayList<>();

        protected AnalyzedMethod(Method method, Listener parent) {
            this.method = method;
            this.parent = parent;
            annotations.addAll(Arrays.asList(method.getAnnotations()));
        }
    }

    private static class EventMethod extends AnalyzedMethod {
        private final EventPriority priority;
        private final Class<? extends Event> eventType;

        private static EventMethod createFrom(AnalyzedMethod method) {
            return new EventMethod(method);
        }

        private EventMethod(AnalyzedMethod method) {
            super(method.method, method.parent);

            this.priority = getPriority();
            this.eventType = getEventType();
        }

        @Nullable
        @SuppressWarnings("unchecked")
        private Class<? extends Event> getEventType() {
            if (method != null && method.getParameterCount() > 0) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length > 0) {
                    Class<?> firstParam = params[0];
                    if (Event.class.isAssignableFrom(firstParam)) {
                        return (Class<? extends Event>) firstParam;
                    }
                }

            }

            return null;
        }

        @Nullable
        private EventPriority getPriority() {
            for (Annotation annotation : this.annotations) {
                if (annotation instanceof EventListener listener) {
                    return listener.priority();
                }
            }

            return null;
        }
    }
}
