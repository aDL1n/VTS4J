package dev.adlin.vts4j.core.event;

public abstract class Event<T extends EventData> {
    private T data;

    public Event(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
