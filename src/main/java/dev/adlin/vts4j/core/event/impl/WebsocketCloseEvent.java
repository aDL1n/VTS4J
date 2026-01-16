package dev.adlin.vts4j.core.event.impl;

import dev.adlin.vts4j.core.event.Event;
import dev.adlin.vts4j.core.network.socket.CloseReason;

public record WebsocketCloseEvent(CloseReason closeReason) implements Event {
}
