package dev.adlin.vts4j.core.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class ClientSocket extends WebSocketClient {

    private Consumer<ServerHandshake> openHandler;
    private Consumer<String> messageHandler;
    private Consumer<CloseReason> closeHandler;
    private Consumer<Throwable> errorHandler;

    public ClientSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        if (openHandler != null) openHandler.accept(handshake);
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) messageHandler.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (closeHandler != null) closeHandler.accept(new CloseReason(code, reason, remote));
    }

    @Override
    public void onError(Exception ex) {
        if (errorHandler != null) errorHandler.accept(ex);
    }

    public void setOpenHandler(Consumer<ServerHandshake> onOpen) {
        this.openHandler = onOpen;
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setCloseHandler(Consumer<CloseReason> onClose) {
        this.closeHandler = onClose;
    }

    public void setErrorHandler(Consumer<Throwable> onError) {
        this.errorHandler = onError;
    }
}
