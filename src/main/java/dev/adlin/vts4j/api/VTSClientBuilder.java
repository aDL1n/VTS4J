package dev.adlin.vts4j.api;

import dev.adlin.vts4j.impl.event.EventHandler;
import dev.adlin.vts4j.api.event.Listener;
import dev.adlin.vts4j.api.event.impl.WebsocketCloseEvent;
import dev.adlin.vts4j.api.event.impl.WebsocketErrorEvent;
import dev.adlin.vts4j.api.event.impl.WebsocketOpenEvent;
import dev.adlin.vts4j.impl.network.NetworkClient;
import dev.adlin.vts4j.impl.request.MessageHandler;
import dev.adlin.vts4j.impl.request.RequestDispatcher;
import dev.adlin.vts4j.impl.VTSClientImpl;

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
        NetworkClient networkClient = new NetworkClient(websocketAddress);
        EventHandler eventHandler = new EventHandler();
        RequestDispatcher requestDispatcher = new RequestDispatcher(networkClient);

        MessageHandler messageHandler = new MessageHandler(requestDispatcher, eventHandler);
        networkClient.setMessageHandler(messageHandler);

        networkClient.setOpenHandler(handshake -> {
            WebsocketOpenEvent event = new WebsocketOpenEvent(handshake);
            eventHandler.callEvent(event);
        });

        networkClient.setCloseHandler(closeReason -> {
            requestDispatcher.closeAll();

            WebsocketCloseEvent event = new WebsocketCloseEvent(closeReason);
            eventHandler.callEvent(event);
        });

        networkClient.setErrorHandler(exception -> {
            WebsocketErrorEvent event = new WebsocketErrorEvent(exception);
            eventHandler.callEvent(event);
        });

        VTSClient client = new VTSClientImpl(
                networkClient,
                eventHandler,
                requestDispatcher
        );

        if (!listeners.isEmpty())
            listeners.forEach(client::registerEventListener);

        return client;
    }
}
