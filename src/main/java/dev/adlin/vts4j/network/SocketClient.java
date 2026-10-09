package dev.adlin.vts4j.network;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Low-level WebSocket client implementation that bridges underlying socket events
 * (open, message, close, error) to configurable functional consumers.
 */
@Slf4j
public class SocketClient extends WebSocketClient {

    private @Nullable Consumer<ServerHandshake> openHandler;
    private @Nullable Consumer<String> messageHandler;
    private @Nullable Consumer<CloseReason> closeHandler;
    private @Nullable Consumer<Exception> errorHandler;

    /**
     * Constructs a new {@code SocketClient} with the specified remote server URI.
     *
     * @param serverUri the raw {@link URI} of the destination WebSocket server
     */
    public SocketClient(final @NonNull URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(final ServerHandshake handshake) {
        log.info("WebSocket handshake completed successfully. Connection is now OPEN to: [{}]", getURI());

        if (openHandler != null) {
            openHandler.accept(handshake);
        } else {
            log.debug("No custom openHandler assigned; skipping extended handshake processing");
        }
    }

    @Override
    public void onMessage(final String message) {
        log.trace("Raw text frame received from socket: {}", message);

        if (messageHandler != null) {
            messageHandler.accept(message);
        } else {
            log.warn("Inbound message dropped. No active messageHandler is configured in the pipeline");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocket connection CLOSED. Code: [{}], Reason: '{}', Remote initiated: [{}]", code, reason, remote);

        if (closeHandler != null) {
            closeHandler.accept(new CloseReason(code, reason, remote));
        } else {
            log.debug("No custom closeHandler assigned; skipping extended closure analysis");
        }
    }

    @Override
    public void onError(final Exception exception) {
        // Передаем exception последним аргументом, чтобы гарантировать логирование Stack Trace в stderr/файл
        log.error("An internal transport or protocol error occurred on the WebSocket connection", exception);

        if (errorHandler != null) {
            errorHandler.accept(exception);
        } else {
            log.debug("No custom errorHandler assigned; exception tracking relies strictly on framework logs");
        }
    }

    /**
     * Sets the handler to be called when the WebSocket connection is opened.
     *
     * @param onOpen the handler to be called on connection open, receives a {@link ServerHandshake} object
     */
    public void setOpenHandler(final @NonNull Consumer<ServerHandshake> onOpen) {
        log.debug("Assigning new custom session open handler callback");
        this.openHandler = onOpen;
    }

    /**
     * Sets the handler to be called when a raw string message frame is received from the WebSocket.
     *
     * @param messageHandler the handler to be called on message receipt, receives the message payload as a String
     */
    public void setMessageHandler(final @NonNull Consumer<String> messageHandler) {
        log.debug("Assigning new custom message dispatcher pipeline callback");
        this.messageHandler = messageHandler;
    }

    /**
     * Sets the handler to be called when the WebSocket connection terminates.
     *
     * @param onClose the handler to be called on connection close, receives a {@link CloseReason} object
     */
    public void setCloseHandler(final @NonNull Consumer<CloseReason> onClose) {
        log.debug("Assigning new custom connection closure analysis callback");
        this.closeHandler = onClose;
    }

    /**
     * Sets the handler to be called when an unhandled exception or protocol error occurs.
     *
     * @param onError the handler to be called on error, receives an {@link Exception} object
     */
    public void setErrorHandler(final @NonNull Consumer<Exception> onError) {
        log.debug("Assigning new custom transport exception interceptor callback");
        this.errorHandler = onError;
    }
}
