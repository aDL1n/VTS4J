package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.MessageHandler;
import dev.adlin.vts4j.core.event.impl.WebsocketCloseEvent;
import dev.adlin.vts4j.core.event.impl.WebsocketErrorEvent;
import dev.adlin.vts4j.core.event.impl.WebsocketOpenEvent;
import dev.adlin.vts4j.core.network.NetworkHandler;
import dev.adlin.vts4j.core.request.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.core.event.*;
import dev.adlin.vts4j.core.request.RequestDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VTSClient {

    private final NetworkHandler networkHandler;
    private final RequestDispatcher requestDispatcher;
    private final EventHandler eventHandler;
    private final MessageHandler messageHandler;

    protected VTSClient(URI vtsAddress) {
        this.networkHandler = new NetworkHandler(vtsAddress);
        this.eventHandler = new EventHandler();
        this.requestDispatcher = new RequestDispatcher(networkHandler);
        this.messageHandler = new MessageHandler(requestDispatcher, eventHandler);

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
    }

    public VTSClient() {
        this(URI.create("ws://localhost:8001"));
    }

    public VTSClient connect(){
        networkHandler.connect();
        return this;
    }

    public VTSClient awaitConnect() {
        networkHandler.awaitConnect();
        return this;
    }

    public void awaitConnect(long timeout, TimeUnit timeUnit) {
        networkHandler.awaitConnect(timeout, timeUnit);
    }

    public void disconnect() {
        networkHandler.disconnect();
    }

    public void disconnectBlocking() {
        networkHandler.awaitDisconnect();
    }

    /**
     * Sends an authentication request with the specified plugin name and author.
     * @param pluginMeta
     */
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

    /**
     * Sends a request to the server and returns a CompletableFuture to dispatch the response.
     * The request is converted to JSON format and sent via a socket.
     * The returned CompletableFuture can be used to obtain the server's response once it is available.
     * @param request object to be sent to the server. This object will be serialized to JSON
     * @return response from the server
     */
    public CompletableFuture<Response> sendRequest(Request request) {
        return requestDispatcher.send(request);
    }

    /**
     * Sends a request with an event type and
     * returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     *
     * @param eventClass the class of event you need to subscribe
     * @param eventConfig additional configuration for the event
     *                    (more details at <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master/Events#events">this page</a>)
     * @return CompletableFuture with a response about the success of the operation
     */
    public CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return sendSubscribeRequest(eventClass, eventConfig, true);
    }

    /**
     * Sends a request with an eventName and config,
     * and returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     * @param eventClass the class of event you need to subscribe
     * @return CompletableFuture with a response about the success of the operation
     */
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

    /**
     * Registers event listener.
     * <p>
     * The registered listener will receive notifications for events that
     * have been subscribed to using the {@link #subscribe(Class<? extends Event>, JsonObject)}
     * or {@link #subscribe(Class<? extends Event>)} method.
     *
     * @param listener an instance of a class implementing the {@link Listener} interface.
     */
    public void registerEventListener(Listener listener) {
        eventHandler.registerListener(listener);
    }
}
