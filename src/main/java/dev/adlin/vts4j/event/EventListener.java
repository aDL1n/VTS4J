package dev.adlin.vts4j.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark methods that act as subscribers for system events.
 * <p>
 * Annotated methods must fulfill the following structural requirements:
 * <ul>
 *     <li>Must be public and non-static.</li>
 *     <li>Must accept exactly one parameter that extends the {@link Event} class.</li>
 * </ul>
 * </p>
 * <p>
 * The execution order of multiple listeners subscribed to the same event type
 * is governed by the defined {@link EventPriority}.
 * </p>
 *
 * @see EventPriority
 * @see EventHandler
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    /**
     * Defines the execution priority of the annotated event listener.
     * <p>
     * Listeners with higher priority values are invoked first by the {@link EventHandler}.
     * </p>
     *
     * @return the execution priority of this listener, defaults to {@link EventPriority#NORMAL}
     */
    EventPriority priority() default EventPriority.NORMAL;
}
