package dev.adlin.vts4j.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.EventHandler;
import dev.adlin.vts4j.event.EventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageHandler implements Consumer<String> {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final Gson gson = new Gson();
    private final RequestDispatcher requestDispatcher;
    private final EventHandler eventHandler;

    public MessageHandler(RequestDispatcher requestDispatcher, EventHandler eventHandler) {
        this.requestDispatcher = requestDispatcher;
        this.eventHandler = eventHandler;
    }

    @Override
    public void accept(String payload) {
        logger.trace("Inbound payload: {}", payload);

        Response response = parseResponse(payload);
        String responseId = response.getRequestId();

        if (requestDispatcher.contains(responseId)) {
            requestDispatcher.dispatch(response);
        } else if (EventRegistry.exists(response.getRequestType())) {
            handleEvent(response);
        }
    }

    private Response parseResponse(String payload) {
        return gson.fromJson(payload, Response.class);
    }

    private void handleEvent(Response response) {
        logger.trace("Handling event");

        try {
            tryHandleEvent(response);
        } catch (Exception exception) {
            logger.error("Failed handle event: {}", exception.getMessage());
        }
    }

    private void tryHandleEvent(Response response) {
        String requestType = response.getRequestType();

        Class<? extends Event> eventClass = EventRegistry.getEventClass(requestType);
        if (eventClass == null) throw new IllegalStateException("Event class not found!");

        Event event = gson.fromJson(response.getData(), eventClass);

        eventHandler.callEvent(event);
    }
}
