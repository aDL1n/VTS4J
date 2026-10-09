package dev.adlin.vts4j.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.exception.APIErrorException;
import dev.adlin.vts4j.network.NetworkClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatches outbound requests, manages tracking futures for expected asynchronous responses,
 * and routes inbound network responses back to their matching execution tracks.
 */
@Slf4j
@RequiredArgsConstructor
public class RequestDispatcher {

    private static final Gson GSON = new Gson();

    /**
     * Map holding active pending request futures awaiting a server response, indexed by request ID.
     */
    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();

    private final @NonNull NetworkClient networkClient;

    /**
     * Dispatches an asynchronous request over the network layer and registers a tracking future
     * to capture the incoming response.
     *
     * @param request the request packet containing a unique ID and payload data
     * @return a {@link CompletableFuture} that resolves to the matching server {@link Response}
     */
    public @NonNull CompletableFuture<Response> send(final @NonNull Request request) {
        log.debug("Preparing to send request. ID: '{}', Type: '{}'", request.id(), request.type());

        final CompletableFuture<Response> future = new CompletableFuture<>();
        this.pendingRequests.put(request.id(), future);

        final String payload = GSON.toJson(request, Request.class);
        try {
            log.trace("Serialized request payload for ID '{}': {}", request.id(), payload);
            this.networkClient.send(payload);
        } catch (Exception exception) {
            this.handleException(request.id(), exception);
        }

        return future;
    }

    /**
     * Handles immediate infrastructure exceptions during transmission by failing the associated future.
     *
     * @param id        the unique request ID
     * @param exception the intercepted transport exception
     */
    private void handleException(final @NonNull String id, final @NonNull Exception exception) {
        log.error("Transport error encountered while sending request ID '{}'", id, exception);

        final CompletableFuture<Response> future = pendingRequests.remove(id);
        if (future != null) {
            future.completeExceptionally(exception);
        }
    }

    /**
     * Routes an incoming response from the network layer to its corresponding pending future track.
     *
     * @param response the parsed server response package
     */
    public void dispatch(final @NonNull Response response) {
        log.trace("Received packet dispatch signal for request ID '{}'", response.requestId());

        final CompletableFuture<Response> future = pendingRequests.remove(response.requestId());
        if (future == null) {
            log.debug("Dropped inbound response packet. Request ID '{}' is unknown or has already timed out", response.requestId());
            return;
        }

        if (this.isErrorResponse(response)) {
            this.handleErrorResponse(future, response);
            return;
        }

        log.debug("Successfully resolved response for request ID '{}'", response.requestId());
        future.complete(response);
    }

    /**
     * Identifies if the server response represents an API-level execution failure.
     *
     * @param response the server response packet
     * @return {@code true} if the response signifies an API error, {@code false} otherwise
     */
    private boolean isErrorResponse(final @NonNull Response response) {
        return "APIError".equals(response.requestType());
    }

    /**
     * Parses the error payload envelope, logs details, and fails the tracking future with an explicit exception.
     *
     * @param future   the future bound to the originating request
     * @param response the server error package wrapper
     */
    private void handleErrorResponse(
            final @NonNull CompletableFuture<Response> future,
            final @NonNull Response response
    ) {
        final JsonObject responsePayload = response.payload();
        if (responsePayload == null) {
            log.error("API error response received for ID '{}', but payload was empty", response.requestId());
            future.completeExceptionally(new APIErrorException("Unknown API error: Empty payload", -1));
            return;
        }

        final String message = responsePayload.get("message").getAsString();
        final int errorId = responsePayload.get("errorID").getAsInt();

        // Исправлено: Добавлена детальная запись контекста ошибки в системные логи
        log.error("API execution failure for request ID '{}'. Error Code: [{}], Message: '{}'",
                response.requestId(), errorId, message);

        final APIErrorException exception = new APIErrorException(message, errorId);
        future.completeExceptionally(exception);
    }

    /**
     * Verifies if a specific request identifier is currently registered and awaiting a server reply.
     *
     * @param requestId the unique request identifier to test
     * @return {@code true} if monitored, {@code false} otherwise
     */
    public boolean contains(final @NonNull String requestId) {
        return this.pendingRequests.containsKey(requestId);
    }

    /**
     * Purges all active pending trackers and forces their exceptional completion.
     * Call this when the transport pipeline unexpectedly disconnects.
     */
    public void closeAll() {
        log.info("Closing dispatcher queue. Cancelling all {} active pending request future(s)", pendingRequests.size());

        final Exception exception = new CancellationException("Connection closed!");
        this.pendingRequests.keySet()
                .forEach(requestId -> {
                    final CompletableFuture<Response> future = pendingRequests.remove(requestId);
                    if (future != null) {
                        future.completeExceptionally(exception);
                    }
                });
    }
}

