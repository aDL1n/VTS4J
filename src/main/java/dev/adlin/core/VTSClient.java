package dev.adlin.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VTSClient {
    public static final Logger LOGGER = Logger.getLogger("VTS4j");

    private final VTSWebSocket webSocket;
    private final ConcurrentHashMap<String, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();


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

        JsonObject authPayload = new JsonObject();
        authPayload.addProperty("apiName", "VTubeStudioPublicAPI");
        authPayload.addProperty("apiVersion", "1.0");
        authPayload.addProperty("requestID", UUID.randomUUID().toString());
        authPayload.addProperty("messageType", "AuthenticationTokenRequest");
        authPayload.add("data", authData);

        return sendRequest(authPayload);
    }

    public CompletableFuture<JsonObject> sendRequest(JsonObject request) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        String requestId = request.get("requestID").getAsString();

        pendingRequests.put(requestId, future);
        webSocket.send(request.toString());

        return future;
    }

    private void messageHandle(String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        String requestId = json.get("requestID").getAsString();

        LOGGER.log(Level.INFO, json.getAsString());

        CompletableFuture<JsonObject> responseFuture = pendingRequests.remove(requestId);
        responseFuture.complete(json);

    }
}
