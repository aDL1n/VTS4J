package dev.adlin.vts4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.RequestBuilder;
import dev.adlin.vts4j.core.VTSWebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class VTSClient {
    public static final Logger LOGGER = Logger.getLogger("VTS4j");

    private final VTSWebSocket webSocket;
    private final ConcurrentHashMap<String, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();
    private final HashMap<String, JsonObject> cachedResponses = new HashMap<>();
    private final Queue<String> idQueue = new ArrayDeque<>();
    private String currentId = null;

    public VTSClient(URI uri) {
        webSocket = new VTSWebSocket(uri);

        webSocket.setOnOpen(null);
        webSocket.setMessageHandler(this::messageHandle);
        webSocket.setOnClose(null);
        webSocket.setOnError(null);
    }

    public void connect(){
        try {
            webSocket.connectBlocking(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public CompletableFuture<JsonObject> authenticate(@NotNull String pluginName, @NotNull String pluginDeveloper, @Nullable String pluginIcon) {
        JsonObject authData = new JsonObject();
        authData.addProperty("pluginName", pluginName);
        authData.addProperty("pluginDeveloper", pluginDeveloper);
        authData.addProperty("pluginIcon", pluginIcon);

        JsonObject authPayload = RequestBuilder.build(
                UUID.randomUUID().toString(),
                "AuthenticationTokenRequest",
                authData
        );

        return sendRequest(authPayload);
    }

    public CompletableFuture<JsonObject> sendRequest(JsonObject request) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        String requestId = request.get("requestID").getAsString();

        idQueue.add(requestId);
        pendingRequests.put(requestId, future);
        webSocket.send(request.toString());

        return future;
    }

    private void messageHandle(String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        String messageType = json.get("messageType").getAsString();
        String requestId = json.get("requestID").getAsString();
        CompletableFuture<JsonObject> responseFuture = pendingRequests.remove(requestId);

        if (currentId == null) currentId = idQueue.poll();

        if (!requestId.equals(currentId)) {
            cachedResponses.put(requestId, json);
        } else {
            if (messageType.equals("APIError")) {
                JsonObject data = json.getAsJsonObject("data");
                String errorId = data.get("errorID").getAsString();
                String errorMessage = data.get("message").getAsString();
                responseFuture.completeExceptionally(new VTSException(errorId + " " + errorMessage));
            } else {
                responseFuture.complete(json);
            }

            checkCache();
        }

        System.out.println(currentId);
        currentId = null;
    }

    private void checkCache() {
        if (cachedResponses.isEmpty()) return;

        messageHandle(cachedResponses.get(idQueue.peek()).getAsString());
    }
}
