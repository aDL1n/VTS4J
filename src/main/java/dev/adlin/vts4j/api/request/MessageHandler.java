package dev.adlin.vts4j.api.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.api.entity.Response;
import dev.adlin.vts4j.api.event.Event;
import dev.adlin.vts4j.api.event.EventHandler;
import dev.adlin.vts4j.api.event.EventRegistry;
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
        } else if (EventRegistry.exists(response.getMessageType())) {
            handleEvent(response);
        }
    }

    private Response parseResponse(String payload) {
        return gson.fromJson(payload, Response.class);
    }

    private void handleEvent(Response response) {
        try {
            tryHandleEvent(response);
        } catch (Exception exception) {
            logger.error("Failed handle event: {}", exception.getMessage());
        }
    }

    private void tryHandleEvent(Response response) {
        String messageType = response.getMessageType();
        Event event = gson.fromJson(response.getData(), EventRegistry.getEventClass(messageType));

        eventHandler.callEvent(event);
    }
}
