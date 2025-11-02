package dev.adlin.vts4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class VTSClient {

    private final ClientSocket socket;
    private final Gson gson = new Gson();

    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();

    public VTSClient(URI vtsAddress) {
        this.socket = new ClientSocket(vtsAddress);

        socket.setOpenHandler(serverHandshake -> System.out.println("Connection opened"));

        socket.setMessageHandler(message -> {
            Response response = parseResponse(message);
            String requestId = response.getRequestId();
            CompletableFuture<Response> future = pendingRequests.remove(requestId);
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

        socket.setErrorHandler(error -> System.out.println("Connection error: " + error));
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

    private Response parseResponse(String json) {
        return gson.fromJson(json, Response.class);
    }

}
