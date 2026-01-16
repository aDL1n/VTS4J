package dev.adlin.vts4j.core.event.impl;

import dev.adlin.vts4j.core.event.Event;

public record WebsocketErrorEvent(Exception exception) implements Event {
}
