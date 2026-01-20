package dev.adlin.vts4j.api;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.api.entity.Response;
import dev.adlin.vts4j.api.event.Event;
import dev.adlin.vts4j.api.auth.AuthenticationProvider;
import dev.adlin.vts4j.api.event.EventHandler;
import dev.adlin.vts4j.api.event.Listener;
import dev.adlin.vts4j.api.entity.Request;
import dev.adlin.vts4j.api.event.SubscriptionProvider;
import dev.adlin.vts4j.api.request.RequestDispatcher;
import dev.adlin.vts4j.api.network.NetworkClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VTSClientImpl implements VTSClient {

    private final NetworkClient networkClient;
    private final EventHandler eventHandler;
    private final RequestDispatcher requestDispatcher;

    private final AuthenticationProvider authenticationProvider;
    private final SubscriptionProvider subscriptionProvider;

    protected VTSClientImpl(NetworkClient networkClient, EventHandler eventHandler, RequestDispatcher requestDispatcher) {
        this.networkClient = networkClient;
        this.eventHandler = eventHandler;
        this.requestDispatcher = requestDispatcher;

        this.authenticationProvider = new AuthenticationProvider(requestDispatcher);
        this.subscriptionProvider = new SubscriptionProvider(requestDispatcher);
    }

    @Override
    public VTSClient connect(){
        networkClient.connect();
        return this;
    }

    @Override
    public VTSClient awaitConnect() {
        networkClient.awaitConnect();
        return this;
    }

    @Override
    public void awaitConnect(long timeout, TimeUnit timeUnit) {
        networkClient.awaitConnect(timeout, timeUnit);
    }

    @Override
    public void disconnect() {
        networkClient.disconnect();
    }

    @Override
    public void disconnectBlocking() {
        networkClient.awaitDisconnect();
    }

    @Override
    public void authenticate(PluginMeta pluginMeta) {
        authenticationProvider.authenticateWithNewToken(pluginMeta);
    }

    @Override
    public void authenticate(PluginMeta pluginMeta, String authToken) {
        authenticationProvider.authenticateWithExistingToken(pluginMeta, authToken);
    }

    @Override
    public CompletableFuture<Response> sendRequest(Request request) {
        return requestDispatcher.send(request);
    }

    @Override
    public CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return this.subscriptionProvider.sendSubscribeRequest(eventClass, eventConfig, true);
    }

    @Override
    public CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass) {
        return this.subscribe(eventClass, null);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return this.subscriptionProvider.sendSubscribeRequest(eventClass, eventConfig, false);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass) {
        return this.unsubscribe(eventClass, null);
    }

    @Override
    public void registerEventListener(Listener listener) {
        eventHandler.registerListener(listener);
    }
}
