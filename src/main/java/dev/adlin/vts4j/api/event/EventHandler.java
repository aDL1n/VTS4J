package dev.adlin.vts4j.api.event;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private final Map<Class<? extends Event>, List<ListenerContainer>> events = new HashMap<>();

    public void callEvent(Event event) {
        logger.trace("Event called: {}", event.getClass().getName());

        if (eventsRegisteredForType(event.getClass())) {
            List<ListenerContainer> eventListeners = new ArrayList<>(events.get(event.getClass()));

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

    public void registerListener(Listener listenerClass) {
        this.registerListenerMethods(this.getEventMethods(listenerClass));
    }

    private void registerListenerMethods(List<EventMethod> eventMethods) {
        for (EventMethod eventMethod : eventMethods) {
            Consumer<Event> listener = (event) -> {
                try {
                    eventMethod.method.invoke(eventMethod.parent, event);
                } catch (Exception e) {
                    logger.error("Failed to register method of event: {}", e.getMessage());
                }
            };

            ListenerContainer container = new ListenerContainer(eventMethod.eventType, listener, eventMethod.priority);
            this.registerListenerContainer(container);
        }
    }

    private void registerListenerContainer(ListenerContainer container) {
        if (!eventsRegisteredForType(container.eventType)) {
            events.put(container.eventType, new ArrayList<>());
        }
        events.get(container.eventType).add(container);
    }

    private List<EventMethod> getEventMethods(Listener listenerClass) {
        List<EventMethod> eventMethods = new ArrayList<>();

        for (Method method : listenerClass.getClass().getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                EventMethod eventMethod = EventMethod.createFrom(new AnalyzedMethod(method, listenerClass));
                if (eventMethod != null && this.hasEventListenerAnnotation(eventMethod))
                    eventMethods.add(eventMethod);
            }
        }

        return eventMethods;
    }

    private boolean hasEventListenerAnnotation(EventMethod eventMethod) {
        for (Annotation annotation : eventMethod.annotations) {
            if (annotation instanceof EventListener) return true;
        }

        return false;
    }

    private boolean eventsRegisteredForType(Class<? extends Event> eventType) {
        if (eventType == null) return false;

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
