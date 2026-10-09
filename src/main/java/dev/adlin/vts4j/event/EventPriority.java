package dev.adlin.vts4j.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the execution priority for event listeners managed by the {@link EventHandler}.
 * <p>
 * The priority determines the order in which listeners are invoked when an event is fired.
 * Higher integer values signify higher execution precedence (invoked earlier in the chain).
 * </p>
 *
 * @see EventListener
 * @see EventHandler
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum EventPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int weight;
}
