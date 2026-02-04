package dev.adlin.vts4j.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.exception.APIErrorException;
import dev.adlin.vts4j.network.NetworkClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RequestDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDispatcher.class);
    private static final Gson GSON = new Gson();

    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();
    private final NetworkClient networkClient;

    public RequestDispatcher(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public @NotNull CompletableFuture<Response> send(final @NotNull Request request) {
        LOGGER.trace("Sending request");

        final CompletableFuture<Response> future = new CompletableFuture<>();
        this.pendingRequests.put(request.getId(), future);

        final String payload = GSON.toJson(request, Request.class);
        try {
            this.networkClient.send(payload);
        } catch (Exception exception) {
            this.handleException(request.getId(), exception);
        }

        return future;
    }

    private void handleException(final @NotNull String id, final @NotNull Exception exception) {
        LOGGER.trace("Request sending handled error");

        final CompletableFuture<Response> future = pendingRequests.remove(id);
        future.completeExceptionally(exception);
    }

    public void dispatch(final @NotNull Response response) {
        LOGGER.trace("Dispatching response");

        final CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());
        if (future == null) return;

        if (this.isErrorResponse(response)) {
            this.handleErrorResponse(future, response);
            return;
        }

        future.complete(response);
    }

    private boolean isErrorResponse(final @NotNull Response response) {
        return "APIError".equals(response.getRequestType());
    }

    private void handleErrorResponse(
            final @NotNull CompletableFuture<Response> future,
            final @NotNull Response response
    ) {
        LOGGER.trace("Error handled while dispatching response");

        final APIErrorException exception = new APIErrorException(
                response.getData().get("message").getAsString(),
                response.getData().get("errorID").getAsInt());

        future.completeExceptionally(exception);
    }

    public boolean contains(final @NotNull String requestId) {
        return this.pendingRequests.containsKey(requestId);
    }

    public void closeAll() {
        LOGGER.trace("Closing all pending requests");

        final Exception exception = new CancellationException("Connection closed!");
        this.pendingRequests.keySet()
                .forEach(requestId -> pendingRequests.remove(requestId).completeExceptionally(exception));
    }
}
