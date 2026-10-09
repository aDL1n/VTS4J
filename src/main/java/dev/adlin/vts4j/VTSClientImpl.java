package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.authentication.AuthenticationProvider;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.EventSubscriptionProvider;
import dev.adlin.vts4j.event.Listener;
import dev.adlin.vts4j.network.NetworkClient;
import dev.adlin.vts4j.request.RequestDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Robust implementation of the {@link VTSClient} interface.
 * <p>
 * This class acts as a structural Facade, encapsulating network topology, request dispatching,
 * authentication layers, and subscription providers into a unified client entity.
 * </p>
 */
@Slf4j
public class VTSClientImpl implements VTSClient {

    private final NetworkClient networkClient;
    private final EventHandler eventHandler;
    private final RequestDispatcher requestDispatcher;

    private final AuthenticationProvider authenticationProvider;
    private final EventSubscriptionProvider eventSubscriptionProvider;

    protected VTSClientImpl(
            final @NonNull NetworkClient networkClient,
            final @NonNull EventHandler eventHandler,
            final @NonNull RequestDispatcher requestDispatcher
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
    public @NonNull CompletableFuture<String> authenticate(final @NonNull PluginMeta pluginMeta) {
        log.info("Initiating full authentication sequence for plugin: '{}'", pluginMeta.name());
        return authenticationProvider.authenticateWithNewToken(pluginMeta);
    }

    @Override
    public void authenticate(
            final @NonNull PluginMeta pluginMeta,
            final @NonNull String authenticationToken
    ) {
        log.info("Initiating token-based direct authentication for plugin: '{}'", pluginMeta.name());
        authenticationProvider.authenticateWithExistingToken(pluginMeta, authenticationToken);
    }

    @Override
    public CompletableFuture<Response> sendRequest(final @NonNull Request request) {
        return requestDispatcher.send(request);
    }

    @Override
    public CompletableFuture<Response> subscribe(
            final @NonNull Class<? extends Event> eventClass,
            final @NonNull JsonObject eventConfig
    ) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, eventConfig, true);
    }

    @Override
    public CompletableFuture<Response> subscribe(final @NonNull Class<? extends Event> eventClass) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, null, true);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(
            final @NonNull Class<? extends Event> eventClass,
            final @Nullable JsonObject eventConfig
    ) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, eventConfig, false);
    }

    @Override
    public CompletableFuture<Response> unsubscribe(final @NonNull Class<? extends Event> eventClass) {
        return eventSubscriptionProvider
                .sendSubscribeRequest(eventClass, null, false);
    }

    @Override
    public void registerEventListener(final @NonNull Listener listener) {
        eventHandler.registerListener(listener);
    }
}
