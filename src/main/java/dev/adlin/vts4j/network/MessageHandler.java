package dev.adlin.vts4j.network;

import com.google.gson.Gson;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.EventRegistry;
import dev.adlin.vts4j.request.RequestDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * Core inbound message broker responsible for intercepting raw text payloads from the network,
 * parsing them into system responses, and routing them either to awaiting request futures
 * or distributing them as system events via the event bus.
 */
@Slf4j
@RequiredArgsConstructor
public class MessageHandler implements Consumer<String> {

    private static final Gson GSON = new Gson();

    private final @NonNull RequestDispatcher requestDispatcher;
    private final @NonNull EventHandler eventHandler;

    /**
     * Intercepts an inbound raw JSON payload from the network socket and orchestrates its routing.
     * <p>
     * If the payload corresponds to an active pending request ID, it is dispatched to the request track.
     * If the payload represents a recognized event identifier, it is processed through the event sub-system.
     * </p>
     *
     * @param payload the raw incoming text message string (usually JSON)
     */
    @Override
    public void accept(final @NonNull String payload) {
        log.trace("Inbound payload {}", payload);

        final Response response = parseResponse(payload);
        final String responseId = response.requestId();

        if (requestDispatcher.contains(responseId)) {
            log.trace("Routing payload as response to pending request ID '{}'", responseId);
            requestDispatcher.dispatch(response);
        } else if (isEvent(response.requestType())) {
            handleEvent(response);
        } else {
            log.debug("Received unrecognized message structure or unhandled request type '{}'", response.requestType());
        }
    }

    /**
     * Checks if the incoming request type name is registered as a valid system event.
     *
     * @param requestTypeName the name identifier of the request type
     * @return {@code true} if the event exists in the registry, {@code false} otherwise
     */
    private static boolean isEvent(final @NonNull String requestTypeName) {
        return EventRegistry.exists(requestTypeName);
    }

    /**
     * Deserializes the raw JSON text payload into a structured system {@link Response} envelope object.
     *
     * @param payload the text message payload
     * @return the deserialized response structure
     */
    private @NonNull Response parseResponse(final @NonNull String payload) {
        return GSON.fromJson(payload, Response.class);
    }

    /**
     * Wraps the event extraction and dispatching logic to catch and log any localized execution failures.
     *
     * @param response the parsed response envelope acting as an event container
     */
    private void handleEvent(final @NonNull Response response) {
        log.debug("Processing inbound event framework packet for type '{}'", response.requestType());

        try {
            tryHandleEvent(response);
        } catch (Exception exception) {
            log.error("Failed to safely process and invoke event handlers for type '{}'", response.requestType(), exception);
        }
    }

    /**
     * Resolves the class type, deserializes the JSON inner payload, and fires the event to the event bus.
     *
     * @param response the incoming response payload
     * @throws IllegalStateException if the event class mapping is missing from the registry
     */
    private void tryHandleEvent(final @NonNull Response response) {
        final Class<? extends Event> eventClass = EventRegistry.getEventClass(response.requestType());
        if (eventClass == null) {
            throw new IllegalStateException("Event class mapping not found in registry for " + response.requestType());
        }

        final Event event = GSON.fromJson(response.payload(), eventClass);
        if (event != null) {
            eventHandler.callEvent(event);
        } else {
            log.debug("Event inner payload was empty or null for type '{}'", response.requestType());
        }
    }
}
