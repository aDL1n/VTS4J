package dev.adlin.vts4j.api.event.impl;

import dev.adlin.vts4j.api.event.Event;
import org.java_websocket.handshake.ServerHandshake;

public record WebsocketOpenEvent(ServerHandshake handshake) implements Event {
}
