package dev.adlin.vts4j.api.event;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.api.entity.Request;
import dev.adlin.vts4j.api.entity.Response;
import dev.adlin.vts4j.api.request.RequestDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SubscriptionProvider {

    private final RequestDispatcher requestDispatcher;

    public SubscriptionProvider(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public CompletableFuture<Response> sendSubscribeRequest(
            @NotNull Class<? extends Event> eventClass,
            @Nullable JsonObject config,
            boolean subscribe
    ) {
        if (!EventRegistry.exists(eventClass))
            throw new IllegalArgumentException("Invalid event name");

        JsonObject payload = new JsonObject();
        payload.addProperty("eventName", EventRegistry.getName(eventClass));
        payload.addProperty("subscribe", subscribe);
        if (config != null) payload.add("config", config);

        Request sibscribeEventRequest = new Request.Builder()
                .setMessageType("EventSubscriptionRequest")
                .setPayload(payload)
                .build();

        return requestDispatcher.send(sibscribeEventRequest);
    }
}
