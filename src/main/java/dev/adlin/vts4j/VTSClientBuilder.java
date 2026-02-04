package dev.adlin.vts4j;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.Listener;
import dev.adlin.vts4j.event.impl.WebsocketCloseEvent;
import dev.adlin.vts4j.event.impl.WebsocketErrorEvent;
import dev.adlin.vts4j.event.impl.WebsocketOpenEvent;
import dev.adlin.vts4j.network.NetworkClient;
import dev.adlin.vts4j.request.MessageHandler;
import dev.adlin.vts4j.request.RequestDispatcher;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VTSClientBuilder {

    private final List<Listener> listeners = new ArrayList<>();

    private URI websocketAddress = URI.create("ws://localhost:8001");

    public static VTSClientBuilder create() {
        return new VTSClientBuilder();
    }

    public VTSClientBuilder setAddress(final @NotNull URI address) {
        this.websocketAddress = address;
        return this;
    }

    public VTSClientBuilder registerListeners(final @NotNull Listener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    public VTSClientBuilder setLoggingLevel(final @NotNull Level level) {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(level);

        return this;
    }

    public VTSClient build() {
        final NetworkClient networkClient = new NetworkClient(websocketAddress);
        final EventHandler eventHandler = new EventHandler();
        final RequestDispatcher requestDispatcher = new RequestDispatcher(networkClient);

        final MessageHandler messageHandler = new MessageHandler(requestDispatcher, eventHandler);
        networkClient.setMessageHandler(messageHandler);

        networkClient.setOpenHandler(handshake -> {
            final WebsocketOpenEvent event = new WebsocketOpenEvent(handshake);
            eventHandler.callEvent(event);
        });

        networkClient.setCloseHandler(closeReason -> {
            requestDispatcher.closeAll();

            final WebsocketCloseEvent event = new WebsocketCloseEvent(closeReason);
            eventHandler.callEvent(event);
        });

        networkClient.setErrorHandler(exception -> {
            final WebsocketErrorEvent event = new WebsocketErrorEvent(exception);
            eventHandler.callEvent(event);
        });

        final VTSClient client = new VTSClientImpl(
                networkClient,
                eventHandler,
                requestDispatcher
        );

        if (!this.listeners.isEmpty())
            this.listeners.forEach(client::registerEventListener);

        return client;
    }
}
