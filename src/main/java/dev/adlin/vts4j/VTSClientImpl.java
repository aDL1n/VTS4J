package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.authentication.AuthenticationProvider;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.Listener;
import dev.adlin.vts4j.event.EventSubscriptionProvider;
import dev.adlin.vts4j.network.NetworkClient;
import dev.adlin.vts4j.request.RequestDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VTSClientImpl implements VTSClient {

    private final NetworkClient networkClient;
    private final EventHandler eventHandler;
    private final RequestDispatcher requestDispatcher;

    private final AuthenticationProvider authenticationProvider;
    private final EventSubscriptionProvider eventSubscriptionProvider;

    protected VTSClientImpl(
            final @NotNull NetworkClient networkClient,
            final @NotNull EventHandler eventHandler,
            final @NotNull RequestDispatcher requestDispatcher
    ) {
        this.networkClient = networkClient;
        this.eventHandler = eventHandler;
        this.requestDispatcher = requestDispatcher;

        this.authenticationProvider = new AuthenticationProvider(requestDispatcher);
        this.eventSubscriptionProvider = new EventSubscriptionProvider(requestDispatcher);
    }

    @Override
    public VTSClient connect() {
        networkClient.connect();
        return this;
    }

    @Override
    public VTSClient awaitConnect() {
        networkClient.awaitConnect();
        return this;
    }

    @Override
    public VTSClient awaitConnect(long timeout, final @NonNull TimeUnit timeUnit) {
        networkClient.awaitConnect(timeout, timeUnit);
        return this;
    }

    @Override
    public void disconnect() {
        networkClient.disconnect();
    }

    @Override
    public void awaitDisconnect() {
        networkClient.awaitDisconnect();
    }

    @Override
    public @NotNull CompletableFuture<String> authenticate(final @NonNull PluginMeta pluginMeta) {
        return authenticationProvider.authenticateWithNewToken(pluginMeta);
    }

    @Override
    public void authenticate(
            final @NonNull PluginMeta pluginMeta,
            final @NonNull String authenticationToken
    ) {
        authenticationProvider.authenticateWithExistingToken(pluginMeta, authenticationToken);
    }

    @Override
    public CompletableFuture<Response> sendRequest(final @NonNull Request request) {
        return requestDispatcher.send(request);
    }

    @Override
    public CompletableFuture<Response> subscribe(
            final @NotNull Class<? extends Event> eventClass,
            final @NotNull JsonObject eventConfig
    ) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, eventConfig, true);
    }

    @Override
    public CompletableFuture<Response> subscribe(final @NotNull Class<? extends Event> eventClass) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, null, true);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(
            final @NotNull Class<? extends Event> eventClass,
            final @Nullable JsonObject eventConfig
    ) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, eventConfig, false);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(final @NotNull Class<? extends Event> eventClass) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, null, false);
    }

    @Override
    public void registerEventListener(final @NonNull Listener listener) {
        eventHandler.registerListener(listener);
    }
}
