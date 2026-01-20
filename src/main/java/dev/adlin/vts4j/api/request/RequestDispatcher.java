package dev.adlin.vts4j.api.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.api.entity.Request;
import dev.adlin.vts4j.api.entity.Response;
import dev.adlin.vts4j.api.exception.APIErrorException;
import dev.adlin.vts4j.api.network.NetworkClient;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RequestDispatcher {

    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final NetworkClient networkClient;

    public RequestDispatcher(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public CompletableFuture<Response> send(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        this.pendingRequests.put(request.getRequestId(), future);

        String payload = gson.toJson(request, Request.class);
        try {
            this.networkClient.send(payload);
        } catch (Exception exception) {
            handleException(request.getRequestId(), exception);
        }

        return future;
    }

    private void handleException(String id, Exception exception) {
        CompletableFuture<Response> future = pendingRequests.remove(id);
        future.completeExceptionally(exception);
    }

    public void dispatch(Response response) {
        CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());
        if (future == null) return;

        if (isErrorResponse(response)) {
            handleErrorResponse(future, response);
            return;
        }

        future.complete(response);
    }

    private boolean isErrorResponse(Response response) {
        return "APIError".equals(response.getMessageType());
    }

    private void handleErrorResponse(CompletableFuture<Response> future, Response response) {
        final APIErrorException exception = new APIErrorException(
                response.getData().get("message").getAsString(),
                response.getData().get("errorID").getAsInt());

        future.completeExceptionally(exception);
    }

    public boolean contains(String requestId) {
        return requestId != null && pendingRequests.containsKey(requestId);
    }

    public void closeAll() {
        Exception exception = new CancellationException("Connection closed!");

        pendingRequests.keySet().forEach(requestId -> pendingRequests.remove(requestId)
                .completeExceptionally(exception));
    }
}
