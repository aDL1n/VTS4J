package dev.adlin.vts4j.event;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EventSubscriptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSubscriptionProvider.class);

    private final RequestDispatcher requestDispatcher;

    public EventSubscriptionProvider(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public CompletableFuture<Response> sendSubscribeRequest(
            final @NotNull Class<? extends Event> eventClass,
            final @NotNull Optional<JsonObject> config,
            boolean subscribe
    ) {
        LOGGER.trace("Sending subscription request");

        if (!EventRegistry.exists(eventClass))
            throw new IllegalArgumentException("Invalid event name");

        final JsonObject payload = PayloadBuilder.builder()
                .addField("eventName", EventRegistry.getName(eventClass))
                .addField("subscribe", subscribe)
                .build();

        config.ifPresent(value -> payload.add("config", value));

        final Request sibscribeEventRequest = RequestBuilder.of(RequestType.EVENT_SUBSCRIPTION)
                .setPayload(payload)
                .build();

        return requestDispatcher.send(sibscribeEventRequest);
    }
}
