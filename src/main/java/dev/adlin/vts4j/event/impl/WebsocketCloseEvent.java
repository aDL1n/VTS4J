package dev.adlin.vts4j.event.impl;

import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.network.CloseReason;

public record WebsocketCloseEvent(CloseReason closeReason) implements Event {
}
