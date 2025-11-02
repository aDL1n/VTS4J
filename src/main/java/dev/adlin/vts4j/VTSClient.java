package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class VTSClient {

    private final ClientSocket socket;

    private final ConcurrentHashMap<String, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    public VTSClient(URI vtsAddress) {
        this.socket = new ClientSocket(vtsAddress);

        socket.setOpenHandler(serverHandshake -> {
            System.out.println("Connection opened");
        });

        socket.setMessageHandler(message -> {
            JsonObject response = JsonParser.parseString(message).getAsJsonObject();
            String requestId = response.get("requestID").getAsString();
            CompletableFuture<JsonObject> future = pendingRequests.remove(requestId);
            if (future != null) {
                future.complete(response);
            }
        });

        socket.setCloseHandler(closeReason -> {
            pendingRequests.forEach((id, future) -> future.completeExceptionally(
                    new RuntimeException("Connection closed")
            ));
            pendingRequests.clear();
        });

        socket.setErrorHandler(error -> {
            System.out.println("Connection error: " + error);
        });
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

        JsonObject authenticationTokenResponse = this.sendRequest(
                RequestBuilder.build(UUID.randomUUID().toString(), "AuthenticationTokenRequest", data)
        ).join();

        String authenticationToken = authenticationTokenResponse.get("data").getAsJsonObject()
                .get("authenticationToken").getAsString();

        JsonObject tokenData = new JsonObject();
        tokenData.addProperty("pluginName", pluginName);
        tokenData.addProperty("pluginDeveloper", pluginDeveloper);
        tokenData.addProperty("authenticationToken", authenticationToken);
        this.sendRequest(RequestBuilder.build(UUID.randomUUID().toString(), "AuthenticationRequest", tokenData)).join();
    }

    public CompletableFuture<JsonObject> sendRequest(JsonObject request) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        String requestId = request.get("requestID").getAsString();
        this.pendingRequests.put(requestId, future);

        this.socket.send(request.toString());
        return future;
    }

}
