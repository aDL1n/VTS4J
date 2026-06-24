package dev.adlin.vts4j.event;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventSubscriptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSubscriptionProvider.class);

    private static final String EVENT_CLASS_NAME_NULL = "Can't find event name by %s class";

    private final @NotNull RequestDispatcher requestDispatcher;

    public @NotNull CompletableFuture<Response> sendSubscribeRequest(
            final @NotNull Class<? extends Event> eventClass,
            final @Nullable JsonObject config,
            boolean subscribe
    ) {
        LOGGER.trace("Sending subscription request");

        if (!EventRegistry.exists(eventClass))
            throw new IllegalArgumentException("Invalid event name");

        String eventName = EventRegistry.getName(eventClass);
        if (eventName == null) {
            return CompletableFuture.failedFuture(new NoSuchElementException(
                    EVENT_CLASS_NAME_NULL.formatted(eventClass.getName())
            ));
        }

        final JsonObject payload = PayloadBuilder.builder()
                .addField("eventName", eventName)
                .addField("subscribe", subscribe)
                .addOfNullable("config", config)
                .build();

        final Request sibscribeEventRequest = RequestBuilder.of(RequestType.EVENT_SUBSCRIPTION)
                .setPayload(payload)
                .build();

        return requestDispatcher.send(sibscribeEventRequest);
    }
}
