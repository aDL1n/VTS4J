package dev.adlin.vts4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.request.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.core.event.*;
import dev.adlin.vts4j.core.socket.ClientSocket;
import dev.adlin.vts4j.exception.APIErrorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class VTSClient {

    private final ClientSocket socket;
    private final Gson gson = new Gson();

    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();
    private final EventHandler eventHandler = new EventHandler();

    public VTSClient(URI vtsAddress) {
        this.socket = new ClientSocket(vtsAddress);

        socket.setOpenHandler(serverHandshake -> System.out.println("Connection opened"));

        socket.setMessageHandler(message -> {
            Response response = parseResponse(message);

            String requestId = response.getRequestId();
            // Check message if is a response to request
            if (isResponseToRequest(requestId)) {
                handlePendingRequest(response);
                return;
            }

            // if this message not a response check may be this message is event
            handleEventMessage(response);
        });

        socket.setCloseHandler(closeReason -> {
            pendingRequests.forEach((id, future) -> future.completeExceptionally(
                    new RuntimeException("Connection closed")
            ));
            pendingRequests.clear();
        });

        socket.setErrorHandler(Throwable::printStackTrace);
    }

    private boolean isResponseToRequest(String requestId) {
        return pendingRequests.containsKey(requestId);
    }

    private void handlePendingRequest(Response response) {
        CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());
        if (future == null) return;

        if (response.getMessageType().equals("APIError")) {
            final APIErrorException exception = new APIErrorException(
                    response.getData().get("message").getAsString(),
                    response.getData().get("errorID").getAsInt());

            future.completeExceptionally(exception);
            throw exception;
        }

        future.complete(response);
    }

    private void handleEventMessage(Response response) {
        Class<? extends Event> eventClass = EventRegistry.getEventClass(response.getMessageType());
        if (eventClass == null)
            throw new NullPointerException("Event type is null");

        Event event = gson.fromJson(response.getData(), eventClass);
        eventHandler.callEvent(event);
    }

    public VTSClient() {
        this(URI.create("ws://localhost:8001"));
    }

    public void connect(){
        this.socket.connect();
    }

    public void connectBlocking() throws InterruptedException{
        this.socket.connectBlocking();
    }

    public void connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.socket.connectBlocking(timeout, timeUnit);
    }

    public void disconnect() {
        this.socket.close();
    }

    public void disconnectBlocking() throws InterruptedException {
        this.socket.closeBlocking();
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
     * Sends a request to the server and returns a CompletableFuture to handle the response.
     * The request is converted to JSON format and sent via a socket.
     * The returned CompletableFuture can be used to obtain the server's response once it is available.
     * @param request object to be sent to the server. This object will be serialized to JSON
     * @return response from the server
     */
    public CompletableFuture<Response> sendRequest(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        this.pendingRequests.put(request.getRequestId(), future);

        try {
            this.socket.send(gson.toJson(request, Request.class));
        } catch (Exception e) {
            pendingRequests.remove(request.getRequestId());
            future.completeExceptionally(e);
        }

        return future;
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
    public CompletableFuture<Response> subscribeToEvent(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return sendSubscribeRequest(eventClass, eventConfig, true);
    }

    /**
     * Sends a request with an eventName and config,
     * and returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     * @param eventClass the class of event you need to subscribe
     * @return CompletableFuture with a response about the success of the operation
     */
    public CompletableFuture<Response> subscribeToEvent(@NotNull Class<? extends Event> eventClass) {
        return this.subscribeToEvent(eventClass, null);
    }

    public CompletableFuture<Response> unsubscribeFromEvent(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig) {
        return sendSubscribeRequest(eventClass, eventConfig, false);
    }

    public CompletableFuture<Response> unsubscribeFromEvent(@NotNull Class<? extends Event> eventClass) {
        return this.unsubscribeFromEvent(eventClass, null);
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
     * have been subscribed to using the {@link #subscribeToEvent(Class<? extends Event>, JsonObject)}
     * or {@link #subscribeToEvent(Class<? extends Event>)} method.
     *
     * @param listener an instance of a class implementing the {@link Listener} interface.
     */
    public void registerEventListener(Listener listener) {
        eventHandler.registerListener(listener);
    }

    private Response parseResponse(String json) {
        return gson.fromJson(json, Response.class);
    }

}
