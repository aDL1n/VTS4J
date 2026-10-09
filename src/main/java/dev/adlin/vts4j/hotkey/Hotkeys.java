package dev.adlin.vts4j.hotkey;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages hotkey operations by loading available hotkeys, triggering executions,
 * and providing a fast-access thread-safe local cache for hotkey information.
 */
@Slf4j
@RequiredArgsConstructor
public class Hotkeys {

    private static final Gson GSON = new Gson();

    private final @NonNull VTSClient client;
    private final Cache<String, Hotkey> cachedHotkeys = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    /**
     * Refreshes the internal cache by fetching the latest hotkeys from VTube Studio.
     * <p>
     * This operation performs an asynchronous network request. Once completed, it purges
     * the existing cache and populates it with the fresh hotkey state. Call this method
     * if hotkeys were modified manually inside the VTube Studio.
     * </p>
     *
     * @return a {@link CompletableFuture} that completes when the local cache is fully updated
     */
    public @NonNull CompletableFuture<Void> refresh() {
        return fetchHotkeys().thenAccept((hotkeys) -> {
            cachedHotkeys.invalidateAll();
            cachedHotkeys.putAll(
                    hotkeys.stream()
                            .collect(Collectors.toMap(Hotkey::id, hotkey -> hotkey))
            );
            log.info("Successfully refreshed hotkey cache. Total cached hotkeys {}", cachedHotkeys.size());
        }).exceptionally(throwable -> {
            log.error("Failed to refresh hotkey cache", throwable);
            throw new RuntimeException(throwable);
        });
    }

    /**
     * Dispatches an asynchronous network request to retrieve all hotkeys bound to the current model.
     *
     * @return a {@link CompletableFuture} containing a list of available {@link Hotkey} objects
     */
    private @NonNull CompletableFuture<List<Hotkey>> fetchHotkeys() {
        log.debug("Fetching hotkeys for the current active model from server...");

        return client.sendRequest(
                RequestBuilder
                        .of(RequestType.HOTKEYS_IN_CURRENT_MODEL)
                        .build()
        ).thenApply(response -> {
            final JsonObject payload = response.payload();
            if (payload == null) {
                log.debug("Received empty payload for current model hotkeys request");
                return Collections.emptyList();
            }

            final JsonElement hotkeysJson = payload.get("availableHotkeys");
            if (hotkeysJson == null || hotkeysJson.isJsonNull()) {
                log.debug("No 'availableHotkeys' field found in response payload");
                return Collections.emptyList();
            }

            final Type hotkeyListType = new TypeToken<List<Hotkey>>() {}.getType();
            final List<Hotkey> hotkeys = GSON.fromJson(hotkeysJson, hotkeyListType);

            log.trace("Deserialized {} hotkey(s) from server response", hotkeys != null ? hotkeys.size() : 0);
            return hotkeys != null ? hotkeys : Collections.emptyList();
        });
    }


    /**
     * Triggers the specified hotkey execution on the remote server.
     *
     * @param hotkey the hotkey instance to be triggered, cannot be null
     * @return a {@link CompletableFuture} that completes when the server acknowledges execution
     */
    public @NonNull CompletableFuture<Void> trigger(final @NonNull Hotkey hotkey) {
        log.info("Triggering hotkey execution: ID='{}', Name='{}'", hotkey.id(), hotkey.name());

        final JsonObject payload = PayloadBuilder.builder()
                .addField("hotkeyID", hotkey.id())
                .build();

        return client.sendRequest(RequestBuilder.of(RequestType.HOTKEY_TRIGGER)
                .setPayload(payload)
                .build()
        ).thenRun(() -> log.debug("Hotkey execution acknowledged by server: ID='{}'", hotkey.id()));
    }

    /**
     * Triggers the hotkey that matches the specified human-readable name.
     *
     * @param hotkeyName the unique name of the hotkey to trigger, cannot be null
     * @return a {@link CompletableFuture} that completes when the server acknowledges execution
     * @throws IllegalArgumentException if no hotkey matches the provided name in the local cache
     */
    public @NonNull CompletableFuture<Void> trigger(final @NonNull String hotkeyName) {
        log.debug("Attempting to trigger hotkey by name: '{}'", hotkeyName);

        final Optional<Hotkey> hotkey = findByName(hotkeyName);
        if (hotkey.isEmpty()) {
            log.error("Failed to trigger hotkey: No hotkey found matching name '{}'", hotkeyName);
            throw new IllegalArgumentException("Hotkey not found");
        }

        return trigger(hotkey.get());
    }

    /**
     * Returns an unmodifiable thread-safe view of the currently cached hotkeys.
     *
     * @return an unmodifiable {@link Map} pairing hotkey IDs with their respective {@link Hotkey} data
     */
    public @NonNull Map<String, Hotkey> getHotkeys() {
        return Collections.unmodifiableMap(cachedHotkeys.asMap());
    }

    /**
     * Searches the local cache for a hotkey with the specified human-readable name.
     *
     * @param hotkeyName the name of the hotkey to look up
     * @return an {@link Optional} containing the matching {@link Hotkey}, or {@link Optional#empty()} if not found
     */
    public @NonNull Optional<Hotkey> findByName(final @NonNull String hotkeyName) {
        log.trace("Searching local cache for hotkey name: '{}'", hotkeyName);

        return cachedHotkeys.asMap().values()
                .stream()
                .filter(hotkey -> hotkey.name().equals(hotkeyName))
                .findFirst();
    }

}
