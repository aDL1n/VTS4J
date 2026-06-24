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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages hotkeys by loading available hotkeys, triggering them, and providing access to hotkey information.
 */
public class Hotkeys {

    private static final Gson GSON = new Gson();

    private final VTSClient client;
    private final Cache<String, Hotkey> cachedHotkeys = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public Hotkeys(final @NotNull VTSClient client) {
        this.client = client;
    }

    /**
     * Refreshes the internal cache by fetching hotkeys from VTube Studio.
     * This performs a blocking network request and overwrites existing cached data.
     * Call this method if hotkeys have been modified in the VTube Studio UI.
     */
    public @NotNull CompletableFuture<Void> refresh() {
        return fetchHotkeys().thenAccept((hotkeys) -> {
            cachedHotkeys.invalidateAll();
            cachedHotkeys.putAll(
                    hotkeys.stream()
                            .collect(Collectors.toMap(Hotkey::id, hotkey -> hotkey))
            );
        });
    }

    private @NotNull CompletableFuture<List<Hotkey>> fetchHotkeys() {
        return client.sendRequest(
                RequestBuilder
                        .of(RequestType.HOTKEYS_IN_CURRENT_MODEL)
                        .build()
        ).thenApply(response -> {
            final JsonObject payload = response.payload();

            if (payload == null) return Collections.emptyList();

            final JsonElement hotkeysJson = payload.get("availableHotkeys");
            System.out.println(hotkeysJson);
            final Type hotkeyListType = new TypeToken<List<Hotkey>>() {}.getType();

            return GSON.fromJson(hotkeysJson, hotkeyListType);
        });
    }


    /**
     * Triggers the specified hotkey by sending a request to the server.
     *
     * @param hotkey The hotkey to be triggered. Cannot be null.
     */
    public @NotNull CompletableFuture<Void> trigger(final @NotNull Hotkey hotkey) {
        final JsonObject payload = PayloadBuilder.builder()
                .addField("hotkeyID", hotkey.id())
                .build();

        return client.sendRequest(RequestBuilder.of(RequestType.HOTKEY_TRIGGER)
                .setPayload(payload)
                .build()
        ).thenAccept(response -> {});
    }

    /**
     * Triggers the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to be triggered. Cannot be null.
     */
    public @NotNull CompletableFuture<Void> trigger(final @NotNull String hotkeyName) {
        final Optional<Hotkey> hotkey = findByName(hotkeyName);
        if (hotkey.isEmpty())
            throw new IllegalArgumentException("Hotkey not found");

        return trigger(hotkey.get());
    }

    /**
     * Returns a map of hotkeys with their IDs as keys.
     *
     * @return A Map containing the hotkeys.
     */
    public @NotNull Map<String, Hotkey> getHotkeys() {
        return Collections.unmodifiableMap(cachedHotkeys.asMap());
    }

    /**
     * Returns the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to retrieve.
     * @return optional with Hotkey object, or null if not found.
     */
    public @NotNull Optional<Hotkey> findByName(final @NotNull String hotkeyName) {
        return cachedHotkeys.asMap().values()
                .stream()
                .filter(hotkey -> hotkey.name().equals(hotkeyName))
                .findFirst();
    }

}
