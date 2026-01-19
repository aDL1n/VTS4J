package dev.adlin.vts4j.impl;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.api.PluginMeta;
import dev.adlin.vts4j.api.response.Response;
import dev.adlin.vts4j.api.VTSClient;
import dev.adlin.vts4j.api.event.Event;
import dev.adlin.vts4j.impl.event.EventHandler;
import dev.adlin.vts4j.impl.event.EventRegistry;
import dev.adlin.vts4j.api.event.Listener;
import dev.adlin.vts4j.api.request.Request;
import dev.adlin.vts4j.impl.request.RequestDispatcher;
import dev.adlin.vts4j.impl.network.NetworkClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VTSClientImpl implements VTSClient {

    private final NetworkClient networkClient;
    private final EventHandler eventHandler;
    private final RequestDispatcher requestDispatcher;

    public VTSClientImpl(NetworkClient networkClient, EventHandler eventHandler, RequestDispatcher requestDispatcher) {
        this.networkClient = networkClient;
        this.eventHandler = eventHandler;
        this.requestDispatcher = requestDispatcher;
    }

    public dev.adlin.vts4j.api.VTSClient connect(){
        networkClient.connect();
        return this;
    }

    public dev.adlin.vts4j.api.VTSClient awaitConnect() {
        networkClient.awaitConnect();
        return this;
    }

    public void awaitConnect(long timeout, TimeUnit timeUnit) {
        networkClient.awaitConnect(timeout, timeUnit);
    }

    public void disconnect() {
        networkClient.disconnect();
    }

    public void disconnectBlocking() {
        networkClient.awaitDisconnect();
    }

    public void authenticate(PluginMeta pluginMeta) {
        String authenticationToken = this.requestAuthenticationToken(pluginMeta);

        JsonObject payload = new JsonObject();
        payload.addProperty("pluginName", pluginMeta.name());
        payload.addProperty("pluginDeveloper", pluginMeta.developer());
        payload.addProperty("authenticationToken", authenticationToken);

        this.sendRequest(
                new Request.Builder()
                        .setMessageType("AuthenticationRequest")
                        .setPayload(payload)
                        .build()
        ).join();
    }

    private String requestAuthenticationToken(PluginMeta pluginMeta) {
        JsonObject payload = new JsonObject();
        payload.addProperty("pluginName", pluginMeta.name());
        payload.addProperty("pluginDeveloper", pluginMeta.developer());
        payload.addProperty("pluginIcon", "");

        Response authenticationTokenResponse = this.sendRequest(
                new Request.Builder()
                        .setMessageType("AuthenticationTokenRequest")
                        .setPayload(payload)
                        .build()
        ).join();

        String authenticationToken = authenticationTokenResponse.getData()
                .get("authenticationToken").getAsString();

        return authenticationToken;
    }

    public CompletableFuture<Response> sendRequest(Request request) {
        return requestDispatcher.send(request);
    }

    public CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return sendSubscribeRequest(eventClass, eventConfig, true);
    }

    public CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass) {
        return this.subscribe(eventClass, null);
    }

    public CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return sendSubscribeRequest(eventClass, eventConfig, false);
    }

    public CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass) {
        return this.unsubscribe(eventClass, null);
    }

    private CompletableFuture<Response> sendSubscribeRequest(
            @NotNull Class<? extends Event> eventClass,
            @Nullable JsonObject config,
            boolean subscribe
    ) {
        if (!EventRegistry.exists(eventClass))
            throw new IllegalArgumentException("Invalid event name");

        JsonObject payload = new JsonObject();
        payload.addProperty("eventName", EventRegistry.getName(eventClass));
        payload.addProperty("subscribe", subscribe);
        if (config != null) payload.add("config", config);

        Request unsibscribeEventRequest = new Request.Builder()
                .setMessageType("EventSubscriptionRequest")
                .setPayload(payload)
                .build();

        return sendRequest(unsibscribeEventRequest);
    }

    public void registerEventListener(Listener listener) {
        eventHandler.registerListener(listener);
    }
}
