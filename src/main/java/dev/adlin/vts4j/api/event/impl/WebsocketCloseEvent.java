package dev.adlin.vts4j.api.event.impl;

import dev.adlin.vts4j.api.event.Event;
import dev.adlin.vts4j.api.network.CloseReason;

public record WebsocketCloseEvent(CloseReason closeReason) implements Event {
}
