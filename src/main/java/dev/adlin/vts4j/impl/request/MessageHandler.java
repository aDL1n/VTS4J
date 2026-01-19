package dev.adlin.vts4j.impl.request;

import com.google.gson.Gson;
import dev.adlin.vts4j.api.response.Response;
import dev.adlin.vts4j.api.event.Event;
import dev.adlin.vts4j.impl.event.EventHandler;
import dev.adlin.vts4j.impl.event.EventRegistry;

import java.util.function.Consumer;

public class MessageHandler implements Consumer<String> {

    private final Gson gson = new Gson();
    private final RequestDispatcher requestDispatcher;
    private final EventHandler eventHandler;

    public MessageHandler(RequestDispatcher requestDispatcher, EventHandler eventHandler) {
        this.requestDispatcher = requestDispatcher;
        this.eventHandler = eventHandler;
    }

    @Override
    public void accept(String payload) {
        Response response = parseResponse(payload);
        String responseId = response.getRequestId();

        if (requestDispatcher.contains(responseId)) {
            requestDispatcher.dispatch(response);
        } else if (EventRegistry.exists(response.getMessageType())) {
            String messageType = response.getMessageType();
            Event event = gson.fromJson(response.getData(), EventRegistry.getEventClass(messageType));

            eventHandler.callEvent(event);
        }
    }

    private Response parseResponse(String payload) {
        return gson.fromJson(payload, Response.class);
    }
}
