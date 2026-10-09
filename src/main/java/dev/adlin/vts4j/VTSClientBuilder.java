package dev.adlin.vts4j;

import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.Listener;
import dev.adlin.vts4j.event.impl.WebsocketCloseEvent;
import dev.adlin.vts4j.event.impl.WebsocketErrorEvent;
import dev.adlin.vts4j.event.impl.WebsocketOpenEvent;
import dev.adlin.vts4j.network.MessageHandler;
import dev.adlin.vts4j.network.NetworkClient;
import dev.adlin.vts4j.request.RequestDispatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fluent builder utility designed to orchestrate dependency injection,
 * wire internal network handlers, and construct fully initialized {@link VTSClient} instances.
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * VTSClient client = VTSClientBuilder.create()
 *         .setAddress(URI.create("ws://127.0.0.1:8001"))
 *         .registerListeners(new MyEventSubscriber())
 *         .build();
 * }</pre>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VTSClientBuilder {

    private final List<Listener> listeners = new ArrayList<>();
    private URI websocketAddress = URI.create("ws://localhost:8001");

    /**
     * Initializes a fresh fluent instance of {@code VTSClientBuilder}.
     *
     * @return a new builder instance
     */
    public static @NonNull VTSClientBuilder create() {
        return new VTSClientBuilder();
    }

    /**
     * Configures the target destination WebSocket address for the VTube Studio API server.
     *
     * @param address the remote server {@link URI}, defaults to {@code ws://localhost:8001}
     * @return this builder instance for method chaining
     */
    public @NonNull VTSClientBuilder setAddress(final @NonNull URI address) {
        this.websocketAddress = address;
        return this;
    }

    /**
     * Registers one or more initial event listener subscribers to the client's event bus upon construction.
     *
     * @param listeners varargs array of custom objects implementing the {@link Listener} interface
     * @return this builder instance for method chaining
     */
    public @NonNull VTSClientBuilder registerListeners(final @NonNull Listener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * Assembles, interconnects, and encapsulates all architectural layers into a ready-to-use {@link VTSClient}.
     * <p>
     * This method automatically maps raw socket-level lifecycle hooks directly into high-level
     * bus events ({@code WebsocketOpenEvent}, {@code WebsocketCloseEvent}, {@code WebsocketErrorEvent})
     * and clears the request dispatcher queue upon sudden network disconnections.
     * </p>
     *
     * @return a fully configured and wired {@link VTSClient} implementation instance
     */
    public @NonNull VTSClient build() {
        log.info("Assembling VTSClient architecture targeting endpoint: [{}]", websocketAddress);

        final NetworkClient networkClient = new NetworkClient(websocketAddress);
        final RequestDispatcher requestDispatcher = new RequestDispatcher(networkClient);
        final EventHandler eventHandler = new EventHandler();

        final MessageHandler messageHandler = new MessageHandler(requestDispatcher, eventHandler);
        networkClient.setMessageHandler(messageHandler);

        networkClient.setOpenHandler(handshake -> {
            log.info("Network tunnel handshake opened; publishing WebsocketOpenEvent to bus");
            final WebsocketOpenEvent event = new WebsocketOpenEvent(handshake);
            eventHandler.callEvent(event);
        });

        networkClient.setCloseHandler(closeReason -> {
            log.warn("Network tunnel closed. Reason '{}'. Evicting pending requests track", closeReason);
            requestDispatcher.closeAll();

            final WebsocketCloseEvent event = new WebsocketCloseEvent(closeReason);
            eventHandler.callEvent(event);
        });

        networkClient.setErrorHandler(exception -> {
            log.error("Internal socket error caught; dispatching execution anomaly event to bus", exception);
            final WebsocketErrorEvent event = new WebsocketErrorEvent(exception);
            eventHandler.callEvent(event);
        });

        final VTSClient client = new VTSClientImpl(
                networkClient,
                eventHandler,
                requestDispatcher
        );

        if (!listeners.isEmpty()) {
            log.debug("Pre-registering {} listener(s) during building phase", listeners.size());
            listeners.forEach(client::registerEventListener);
        }

        log.info("VTSClient module assembly completed successfully");
        return client;
    }
}

