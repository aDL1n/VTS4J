package dev.adlin.vts4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.core.event.*;
import dev.adlin.vts4j.core.socket.ClientSocket;
import dev.adlin.vts4j.exception.APIErrorException;
import dev.adlin.vts4j.core.event.EventType;
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
            String messageType = response.getMessageType();

            // Check message if is a response to request
            if (pendingRequests.containsKey(requestId)) {
                CompletableFuture<Response> future = pendingRequests.remove(requestId);
                if (future == null) return;

                switch (messageType) {
                    case "APIError" -> {
                        final APIErrorException exception = new APIErrorException(
                                response.getData().get("message").getAsString(),
                                response.getData().get("errorID").getAsInt());

                        future.completeExceptionally(exception);
                        throw exception;
                    }
                }

                future.complete(response);
            }

            // if this message not a response check may be this message is event
            EventType type = EventType.valueOfName(messageType);
            if (type == null)
                throw new NullPointerException("Event type is null");

            Class<? extends Event> eventClass = EventRegistry.getEventClass(type);
            Event event = gson.fromJson(response.getData(), eventClass);

            eventHandler.callEvent(event);
        });

        socket.setCloseHandler(closeReason -> {
            pendingRequests.forEach((id, future) -> future.completeExceptionally(
                    new RuntimeException("Connection closed")
            ));
            pendingRequests.clear();
        });

        socket.setErrorHandler(Throwable::printStackTrace);
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
     * @param pluginName the name of your plugin
     * @param pluginDeveloper plugin developer
     */
    public void authenticate(String pluginName, String pluginDeveloper) {
        JsonObject data = new JsonObject();
        data.addProperty("pluginName", pluginName);
        data.addProperty("pluginDeveloper", pluginDeveloper);
        data.addProperty("pluginIcon", "");

        Response authenticationTokenResponse = this.sendRequest(
                new Request.Builder()
                        .setMessageType("AuthenticationTokenRequest")
                        .setData(data)
                        .build()
        ).join();

        String authenticationToken = authenticationTokenResponse.getData()
                .get("authenticationToken").getAsString();

        JsonObject tokenData = new JsonObject();
        tokenData.addProperty("pluginName", pluginName);
        tokenData.addProperty("pluginDeveloper", pluginDeveloper);
        tokenData.addProperty("authenticationToken", authenticationToken);

        this.sendRequest(
                new Request.Builder()
                        .setMessageType("AuthenticationRequest")
                        .setData(tokenData)
                        .build()
        ).join();
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

        this.socket.send(gson.toJson(request, Request.class));
        return future;
    }

    /**
     * Sends a request with an event type and
     * returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     *
     * @param event the type of event you need to register from EventType
     * @param eventConfig additional configuration for the event
     *                    (more details at <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master/Events#events">this page</a>)
     * @return CompletableFuture with a response about the success of the operation
     */
    public CompletableFuture<Response> subscribeToEvent(EventType event, @Nullable JsonObject eventConfig) {
        CompletableFuture<Response> future = new CompletableFuture<>();

        JsonObject data = new JsonObject();
        data.addProperty("eventName", event.getName());
        data.addProperty("subscribe", true);
        if (eventConfig != null) data.add("config", eventConfig);

        Request registerEventRequest = new Request.Builder()
                .setMessageType("EventSubscriptionRequest")
                .setData(data)
                .build();

        this.pendingRequests.put(registerEventRequest.getRequestId(), future);

        this.socket.send(gson.toJson(registerEventRequest, Request.class));
        return future;
    }

    /**
     * Sends a request with an event type and config,
     * and returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     * @param event the type of event you need to register from EventType
     * @return CompletableFuture with a response about the success of the operation
     */
    public CompletableFuture<Response> subscribeToEvent(EventType event) {
        return this.subscribeToEvent(event, null);
    }

    /**
     * Registers event listener.
     * <p>
     * The registered listener will receive notifications for events that
     * have been subscribed to using the {@link #subscribeToEvent(EventType, JsonObject)}
     * or {@link #subscribeToEvent(EventType)} method.
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
