package dev.adlin.vts4j.network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.function.Consumer;

public class SocketClient extends WebSocketClient {

    private Consumer<ServerHandshake> openHandler;
    private Consumer<String> messageHandler;
    private Consumer<CloseReason> closeHandler;
    private Consumer<Exception> errorHandler;

    public SocketClient(final @NotNull URI serverUri) {
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
    public void onError(Exception exception) {
        if (errorHandler != null) errorHandler.accept(exception);
    }

    /**
     * Sets the handler to be called when the WebSocket connection is opened.
     * The provided Consumer will receive a ServerHandshake object containing details about the handshake.
     *
     * @param onOpen The handler to be called on connection open. Receives a ServerHandshake object.
     */
    public void setOpenHandler(final @NotNull Consumer<ServerHandshake> onOpen) {
        this.openHandler = onOpen;
    }

    /**
     * Sets the handler to be called when a message is received from the WebSocket.
     * The provided Consumer will receive the message as a String.
     *
     * @param messageHandler The handler to be called on message receipt. Receives the message as a String.
     */
    public void setMessageHandler(final @NotNull Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Sets the handler to be called when the WebSocket connection is closed.
     * The provided Consumer will receive a CloseReason object containing details about the closure.
     *
     * @param onClose The handler to be called on connection close. Receives a CloseReason object.
     */
    public void setCloseHandler(final @NotNull Consumer<CloseReason> onClose) {
        this.closeHandler = onClose;
    }

    /**
     * Sets the handler to be called when an error occurs in the WebSocket connection.
     * The provided Consumer will receive a Throwable object representing the error.
     *
     * @param onError The handler to be called on error. Receives a Throwable object.
     */
    public void setErrorHandler(final @NotNull Consumer<Exception> onError) {
        this.errorHandler = onError;
    }
}
