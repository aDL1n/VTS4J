package dev.adlin.vts4j.api.event.impl;

import dev.adlin.vts4j.api.event.Event;

public record WebsocketErrorEvent(Exception exception) implements Event {
}
