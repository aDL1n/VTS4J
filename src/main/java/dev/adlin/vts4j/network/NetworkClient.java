package dev.adlin.vts4j.network;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NetworkClient {

    private final SocketClient socket;

    public NetworkClient(URI address) {
        this.socket = new SocketClient(address);
    }

    public void awaitConnect() {
        try {
            socket.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", e);
        }
    }

    public void awaitConnect(long timeout, TimeUnit timeUnit) {
        try {
            socket.connectBlocking(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", e);
        }
    }

    public void connect() {
        socket.connect();
    }

    public void awaitDisconnect() {
        try {
            socket.closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Disconnection interrupted", e);
        }
    }

    public void disconnect() {
        socket.close();
    }

    public void send(String payload) {
        socket.send(payload);
    }

    public void setMessageHandler(Consumer<String> handleMessage) {
        socket.setMessageHandler(handleMessage);
    }

    public void setErrorHandler(Consumer<Exception> handler) {
        socket.setErrorHandler(handler);
    }

    public void setOpenHandler(Consumer<ServerHandshake> handshake) {
        socket.setOpenHandler(handshake);
    }

    public void setCloseHandler(Consumer<CloseReason> closeReason) {
        socket.setCloseHandler(closeReason);
    }
}
