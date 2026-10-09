package dev.adlin.vts4j.network;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * High-level network client that delegates infrastructure operations to an underlying socket client.
 * <p>
 * Implements both asynchronous and synchronous (blocking) connection lifecycle management methods,
 * alongside payload dispatching and custom event hook registrations.
 * </p>
 */
@Slf4j
public class NetworkClient {

    private final SocketClient socket;

    public NetworkClient(final @NonNull URI address) {
        this.socket = new SocketClient(address);
    }

    /**
     * Establishes a synchronous connection to the remote server, blocking the calling thread
     * indefinitely until the handshake completes or fails.
     *
     * @throws RuntimeException if the thread is interrupted while waiting to connect
     */
    public void awaitConnect() {
        log.info("Attempting synchronous connection blocking call...");
        try {
            socket.connectBlocking();
            log.info("Synchronous connection established successfully");
        } catch (InterruptedException exception) {
            log.error("Thread interrupted while executing blocking connection task", exception);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", exception);
        }
    }

    /**
     * Establishes a synchronous connection to the remote server, blocking the calling thread
     * up to the specified timeout interval.
     *
     * @param timeout  the maximum duration to wait for a connection
     * @param timeUnit the time unit of the timeout argument
     * @throws RuntimeException if the thread is interrupted while waiting to connect
     */
    public void awaitConnect(long timeout, final @NonNull TimeUnit timeUnit) {
        log.info("Attempting synchronous connection call with timeout: {} {}", timeout, timeUnit);
        try {
            boolean connected = socket.connectBlocking(timeout, timeUnit);
            if (connected) {
                log.info("Synchronous connection established within specified timeout limit");
            } else {
                log.warn("Connection attempt timed out after {} {}", timeout, timeUnit);
            }
        } catch (InterruptedException exception) {
            log.error("Thread interrupted during timed blocking connection task", exception);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Connection interrupted", exception);
        }
    }

    /**
     * Initiates a non-blocking background connection routine to the remote server.
     */
    public void connect() {
        log.info("Initiating asynchronous connection routine in background thread...");
        socket.connect();
    }

    /**
     * Synchronously tears down the active socket session, blocking the calling thread
     * until the close handshake is acknowledged.
     *
     * @throws RuntimeException if the thread is interrupted while executing the close procedure
     */
    public void awaitDisconnect() {
        log.info("Attempting synchronous disconnection blocking close call...");
        try {
            socket.closeBlocking();
            log.info("Synchronous disconnection completed successfully");
        } catch (InterruptedException exception) {
            log.error("Thread interrupted while executing blocking disconnect task", exception);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Disconnection interrupted", exception);
        }
    }

    /**
     * Initiates an asynchronous non-blocking disconnection routine.
     */
    public void disconnect() {
        log.info("Initiating asynchronous disconnect routine...");
        socket.close();
    }

    /**
     * Transmits a raw string payload over the active socket session.
     *
     * @param payload the message payload string to send to the server
     */
    public void send(final @NonNull String payload) {
        log.trace("Outbound payload transmitted over socket: {}", payload);
        socket.send(payload);
    }

    /**
     * Assigns the main core callback interceptor for incoming data packets.
     *
     * @param handleMessage functional consumer handling inbound raw strings
     */
    public void setMessageHandler(final @NonNull Consumer<String> handleMessage) {
        log.debug("Configuring customized inbound text message consumer pipeline");
        socket.setMessageHandler(handleMessage);
    }

    /**
     * Assigns a custom error monitoring callback hook.
     *
     * @param handler functional consumer handling raw network or protocol exceptions
     */
    public void setErrorHandler(final @NonNull Consumer<Exception> handler) {
        log.debug("Configuring customized network exception handler pipeline");
        socket.setErrorHandler(handler);
    }

    /**
     * Assigns a callback hook triggered upon successful completion of the opening handshake.
     *
     * @param handshake functional consumer processing handshake details
     */
    public void setOpenHandler(final @NonNull Consumer<ServerHandshake> handshake) {
        log.debug("Configuring customized server handshake success interception hook");
        socket.setOpenHandler(handshake);
    }

    /**
     * Assigns a callback hook triggered immediately after the socket session terminates.
     *
     * @param closeReason functional consumer analyzing closure state parameters
     */
    public void setCloseHandler(final @NonNull Consumer<CloseReason> closeReason) {
        log.debug("Configuring customized socket closure reason analyzer hook");
        socket.setCloseHandler(closeReason);
    }
}
