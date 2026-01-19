package dev.adlin.vts4j.api.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    EventPriority priority() default EventPriority.NORMAL;
}
