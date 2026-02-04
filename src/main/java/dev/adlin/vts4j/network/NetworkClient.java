package dev.adlin.vts4j.network;

import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NetworkClient {

    private final SocketClient socket;

    public NetworkClient(final @NotNull URI address) {
        this.socket = new SocketClient(address);
    }

    public void awaitConnect() {
        try {
            this.socket.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", e);
        }
    }

    public void awaitConnect(long timeout, final @NotNull TimeUnit timeUnit) {
        try {
            this.socket.connectBlocking(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", e);
        }
    }

    public void connect() {
        this.socket.connect();
    }

    public void awaitDisconnect() {
        try {
            this.socket.closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Disconnection interrupted", e);
        }
    }

    public void disconnect() {
        this.socket.close();
    }

    public void send(final @NotNull String payload) {
        this.socket.send(payload);
    }

    public void setMessageHandler(final @NotNull Consumer<String> handleMessage) {
        this.socket.setMessageHandler(handleMessage);
    }

    public void setErrorHandler(final @NotNull Consumer<Exception> handler) {
        this.socket.setErrorHandler(handler);
    }

    public void setOpenHandler(final @NotNull Consumer<ServerHandshake> handshake) {
        this.socket.setOpenHandler(handshake);
    }

    public void setCloseHandler(final @NotNull Consumer<CloseReason> closeReason) {
        this.socket.setCloseHandler(closeReason);
    }
}
