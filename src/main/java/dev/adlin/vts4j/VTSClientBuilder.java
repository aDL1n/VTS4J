package dev.adlin.vts4j;

import dev.adlin.vts4j.core.MessageHandler;
import dev.adlin.vts4j.core.event.EventHandler;
import dev.adlin.vts4j.core.event.Listener;
import dev.adlin.vts4j.core.event.impl.WebsocketCloseEvent;
import dev.adlin.vts4j.core.event.impl.WebsocketErrorEvent;
import dev.adlin.vts4j.core.event.impl.WebsocketOpenEvent;
import dev.adlin.vts4j.core.network.NetworkHandler;
import dev.adlin.vts4j.core.request.RequestDispatcher;

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

    public VTSClientBuilder setAddress(URI address) {
        this.websocketAddress = address;
        return this;
    }

    public VTSClientBuilder registerListeners(Listener... listeners) {
        if (listeners == null) return this;

        Collections.addAll(this.listeners, listeners);
        return this;
    }

    public VTSClient build() {
        NetworkHandler networkHandler = new NetworkHandler(websocketAddress);
        EventHandler eventHandler = new EventHandler();
        RequestDispatcher requestDispatcher = new RequestDispatcher(networkHandler);

        MessageHandler messageHandler = new MessageHandler(requestDispatcher, eventHandler);
        networkHandler.setMessageHandler(messageHandler);

        networkHandler.onOpen(handshake -> {
            WebsocketOpenEvent event = new WebsocketOpenEvent(handshake);
            eventHandler.callEvent(event);
        });

        networkHandler.onClose(closeReason -> {
            requestDispatcher.closeAll();

            WebsocketCloseEvent event = new WebsocketCloseEvent(closeReason);
            eventHandler.callEvent(event);
        });

        networkHandler.setErrorHandler(exception -> {
            WebsocketErrorEvent event = new WebsocketErrorEvent(exception);
            eventHandler.callEvent(event);
        });

        VTSClient client = new VTSClient(
                networkHandler,
                eventHandler,
                requestDispatcher
        );

        if (!listeners.isEmpty())
            listeners.forEach(client::registerEventListener);

        return client;
    }
}
