package dev.adlin.vts4j.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.EventRegistry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageHandler implements Consumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private static final Gson GSON = new Gson();

    private final RequestDispatcher requestDispatcher;
    private final EventHandler eventHandler;

    public MessageHandler(
            final @NotNull RequestDispatcher requestDispatcher,
            final @NotNull EventHandler eventHandler
    ) {
        this.requestDispatcher = requestDispatcher;
        this.eventHandler = eventHandler;
    }

    @Override
    public void accept(final @NotNull String payload) {
        LOGGER.trace("Inbound payload: {}", payload);

        final Response response = parseResponse(payload);
        final String responseId = response.getRequestId();

        if (this.requestDispatcher.contains(responseId)) {
            this.requestDispatcher.dispatch(response);
        } else if (EventRegistry.exists(response.getRequestType())) {
            this.handleEvent(response);
        }
    }

    private @NotNull Response parseResponse(final @NotNull String payload) {
        return GSON.fromJson(payload, Response.class);
    }

    private void handleEvent(final @NotNull Response response) {
        LOGGER.trace("Handling event");

        try {
            this.tryHandleEvent(response);
        } catch (Exception exception) {
            LOGGER.error("Failed handle event: {}", exception.getMessage());
        }
    }

    private void tryHandleEvent(final @NotNull Response response) {
        final String requestType = response.getRequestType();

        final Class<? extends Event> eventClass = EventRegistry.getEventClass(requestType);
        if (eventClass == null) throw new IllegalStateException("Event class not found!");

        final Event event = GSON.fromJson(response.getData(), eventClass);
        this.eventHandler.callEvent(event);
    }
}
