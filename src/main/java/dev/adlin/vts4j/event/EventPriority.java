package dev.adlin.vts4j.event;

import lombok.Getter;

@Getter
public enum EventPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int id;

    EventPriority(int id) {
        this.id = id;
    }
}
