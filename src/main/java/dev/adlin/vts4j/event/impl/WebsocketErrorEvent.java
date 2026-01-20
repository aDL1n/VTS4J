package dev.adlin.vts4j.event.impl;

import dev.adlin.vts4j.event.Event;

public record WebsocketErrorEvent(Exception exception) implements Event {
}
