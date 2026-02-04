package dev.adlin.vts4j.event;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class SubscriptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionProvider.class);

    private final RequestDispatcher requestDispatcher;

    public SubscriptionProvider(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public CompletableFuture<Response> sendSubscribeRequest(
            final @NotNull Class<? extends Event> eventClass,
            final @Nullable JsonObject config,
            boolean subscribe
    ) {
        LOGGER.trace("Sending subscription request");

        if (!EventRegistry.exists(eventClass))
            throw new IllegalArgumentException("Invalid event name");

        JsonObject payload = new JsonObject();
        payload.addProperty("eventName", EventRegistry.getName(eventClass));
        payload.addProperty("subscribe", subscribe);
        if (config != null) payload.add("config", config);

        final Request sibscribeEventRequest = RequestBuilder.of(RequestType.EVENT_SUBSCRIPTION)
                .setPayload(payload)
                .build();

        return requestDispatcher.send(sibscribeEventRequest);
    }
}
