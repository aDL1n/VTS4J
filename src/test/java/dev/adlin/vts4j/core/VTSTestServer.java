package dev.adlin.vts4j.core;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

public class VTSTestServer extends WebSocketServer {

    BiConsumer<WebSocket, String> messageHandler;

    public VTSTestServer (InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        messageHandler.accept(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    public void setMessageHandler(BiConsumer<WebSocket, String> messageHandler) {
        this.messageHandler = messageHandler;
    }}
