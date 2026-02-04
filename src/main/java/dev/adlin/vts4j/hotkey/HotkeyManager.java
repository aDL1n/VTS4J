package dev.adlin.vts4j.hotkey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages hotkeys by loading available hotkeys, triggering them, and providing access to hotkey information.
 */
public class HotkeyManager {

    private static final Gson GSON = new Gson();

    private final VTSClient client;
    private final ConcurrentHashMap<String, Hotkey> cachedHotkeys = new ConcurrentHashMap<>();

    public HotkeyManager(final @NotNull VTSClient client) {
        this.client = client;
    }

    /**
     * Refreshes the internal cache by fetching hotkeys from VTube Studio.
     * This performs a blocking network request and overwrites existing cached data.
     * Call this method if hotkeys have been modified in the VTube Studio UI.
     */
    public void refresh() {
        final List<Hotkey> fetchedHotkeys = this.fetchHotkeys();

        this.cachedHotkeys.clear();
        this.cachedHotkeys.putAll(fetchedHotkeys.stream().collect(
                        Collectors.toMap(Hotkey::id, hotkey -> hotkey)));
    }

    private @NotNull List<Hotkey> fetchHotkeys() {
        final Response response = client.sendRequest(
                RequestBuilder
                        .of(RequestType.HOTKEYS_IN_CURRENT_MODEL)
                        .build()).join();

        final JsonObject responseData = response.getData();
        return responseData.getAsJsonArray("availableHotkeys").asList().stream()
                .map(rawHotkey -> GSON.fromJson(rawHotkey, Hotkey.class))
                .toList();
    }

    /**
     * Triggers the specified hotkey by sending a request to the server.
     *
     * @param hotkey The hotkey to be triggered. Cannot be null.
     */
    public void trigger(final @NotNull Hotkey hotkey) {
        final JsonObject payload = new JsonObject();
        payload.addProperty("hotkeyID", hotkey.id());

        this.client.sendRequest(RequestBuilder.of(RequestType.HOTKEY_TRIGGER)
                .setPayload(payload)
                .build()
        );
    }

    /**
     * Triggers the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to be triggered. Cannot be null.
     */
    public void trigger(final @NotNull String hotkeyName) {
        final Optional<Hotkey> hotkey = this.findByName(hotkeyName);
        if (hotkey.isEmpty())
            throw new NullPointerException("Hotkey not found");

        this.trigger(hotkey.orElse(null));
    }

    /**
     * Returns a map of hotkeys with their IDs as keys.
     *
     * @return A Map containing the hotkeys.
     */
    public @NotNull Map<String, Hotkey> getHotkeys() {
        return Collections.unmodifiableMap(this.cachedHotkeys);
    }

    /**
     * Returns the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to retrieve.
     * @return optional with Hotkey object, or null if not found.
     */
    public @NotNull Optional<Hotkey> findByName(final @NotNull String hotkeyName) {
        return this.cachedHotkeys.values().stream()
                .filter(hotkey -> hotkey.name().equals(hotkeyName))
                .findFirst();
    }

}
