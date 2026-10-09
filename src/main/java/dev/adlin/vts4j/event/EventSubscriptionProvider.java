package dev.adlin.vts4j.event;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

/**
 * Provider responsible for managing event subscriptions by sending subscribe or
 * unsubscribe requests to the remote server via a request dispatcher.
 */
@Slf4j
@RequiredArgsConstructor
public class EventSubscriptionProvider {

    private static final String EVENT_CLASS_NAME_NULL = "Can't find event name by %s class";

    private final @NonNull RequestDispatcher requestDispatcher;

    /**
     * Sends an asynchronous event subscription or unsubscription request to the remote server.
     *
     * @param eventClass the class type of the event to subscribe or unsubscribe from
     * @param config     optional configuration payload for the event subscription, can be {@code null}
     * @param subscribe  {@code true} to subscribe, {@code false} to unsubscribe
     * @return a {@link CompletableFuture} containing the server response acknowledgment
     * @throws IllegalArgumentException if the provided event class is not registered in the {@link EventRegistry}
     */
    public @NonNull CompletableFuture<Response> sendSubscribeRequest(
            final @NonNull Class<? extends Event> eventClass,
            final @Nullable JsonObject config,
            boolean subscribe
    ) {
        log.debug(
                "Preparing event subscription request. Action: '{}', Event Class: '{}'",
                subscribe ? "SUBSCRIBE" : "UNSUBSCRIBE",
                eventClass.getSimpleName()
        );

        if (!EventRegistry.exists(eventClass)){
            log.error(
                    "Subscription failed: Event class '{}' is not registered in EventRegistry",
                    eventClass.getName()
            );
            throw new IllegalArgumentException("Invalid event name");
        }

        String eventName = EventRegistry.getName(eventClass);
        if (eventName == null) {
            final String errorMessage = EVENT_CLASS_NAME_NULL.formatted(eventClass.getName());
            log.error("Subscription failed: {}", errorMessage);

            return CompletableFuture.failedFuture(
                    new NoSuchElementException(errorMessage)
            );
        }

        final JsonObject payload = PayloadBuilder.builder()
                .addField("eventName", eventName)
                .addField("subscribe", subscribe)
                .addOfNullable("config", config)
                .build();

        final Request subscribeEventRequest = RequestBuilder.of(RequestType.EVENT_SUBSCRIPTION)
                .setPayload(payload)
                .build();

        log.trace("Dispatching subscription request to dispatcher: {}", subscribeEventRequest);

        return requestDispatcher.send(subscribeEventRequest);
    }
}
