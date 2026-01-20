package dev.adlin.vts4j.event;

public enum EventPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int id;

    EventPriority(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
