package dev.adlin.vts4j;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.socket.ClientSocket;
import dev.adlin.vts4j.core.EventListener;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.exception.APIErrorException;
import dev.adlin.vts4j.type.EventType;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class VTSClient {

    private final ClientSocket socket;
    private final Gson gson = new Gson();

    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();

    // registered event types
    private final Set<String> registeredEvents = new HashSet<>();

    private dev.adlin.vts4j.core.EventListener eventListener;

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
                    case "EventSubscriptionResponse" -> {
                        List<String> subscribedEvents = response.getData().get("subscribedEvents")
                                .getAsJsonArray().asList()
                                .stream().map(JsonElement::getAsString).toList();

                        registeredEvents.addAll(subscribedEvents);
                    }
                    case null, default -> {
                    }
                }

                future.complete(response);
            }
            // if this message not a response check may be this message is event
            else if (eventListener != null && registeredEvents.contains(messageType)) {
                EventType type = EventType.valueOfName(response.getMessageType());

                eventListener.onEvent(type, response.getData());
            }
        });

        socket.setCloseHandler(closeReason -> {
            pendingRequests.forEach((id, future) -> future.completeExceptionally(
                    new RuntimeException("Connection closed")
            ));
            pendingRequests.clear();
        });

        socket.setErrorHandler(error -> System.out.println("Connection error: " + Arrays.toString(error.getStackTrace())));
    }

    public VTSClient() {
        this(URI.create("ws://localhost:8001"));
    }

    public void connect() {
        this.socket.connect();
    }

    public void connectBlocking() throws InterruptedException {
        this.socket.connectBlocking();
    }

    public void connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.socket.connectBlocking(timeout, timeUnit);
    }

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

    public CompletableFuture<Response> sendRequest(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        this.pendingRequests.put(request.getRequestId(), future);

        this.socket.send(gson.toJson(request, Request.class));
        return future;
    }

    public CompletableFuture<Response> registerEvent(EventType event, @Nullable JsonObject eventConfig) {
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

    public CompletableFuture<Response> registerEvent(EventType event) {
        return this.registerEvent(event, null);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    private Response parseResponse(String json) {
        return gson.fromJson(json, Response.class);
    }

}
