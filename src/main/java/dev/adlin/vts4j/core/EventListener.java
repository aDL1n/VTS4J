package dev.adlin.vts4j.core;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.type.EventType;

@FunctionalInterface
public interface EventListener {
    void onEvent(EventType eventType, JsonObject data);
}
