package dev.adlin.core;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class VTSWebSocket extends WebSocketClient {
    private Consumer<String> messageHandler;
    private Consumer<Exception> onError;
    private Consumer<String> onClose;
    private Runnable onOpen;

    public VTSWebSocket(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        if (onOpen != null) onOpen.run();
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) messageHandler.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (onClose != null) onClose.accept(reason);
    }

    @Override
    public void onError(Exception ex) {
        if (onError != null) onError.accept(ex);
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setOnError(Consumer<Exception> onError) {
        this.onError = onError;
    }

    public void setOnClose(Consumer<String> onClose) {
        this.onClose = onClose;
    }

    public void setOnOpen(Runnable onOpen) {
        this.onOpen = onOpen;
    }
}
