package dev.adlin.vts4j.event.impl;

import dev.adlin.vts4j.event.Event;
import org.java_websocket.handshake.ServerHandshake;

public record WebsocketOpenEvent(ServerHandshake handshake) implements Event {
}
