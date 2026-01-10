package dev.adlin.vts4j.core.hotkey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.core.request.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.core.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages hotkeys by loading available hotkeys, triggering them, and providing access to hotkey information.
 */
public class HotkeyManager {

    private final VTSClient client;
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<String, Hotkey> cachedHotkeys = new ConcurrentHashMap<>();

    public HotkeyManager(VTSClient client) {
        this.client = client;
    }

    /**
     * Refreshes the internal cache by fetching hotkeys from VTube Studio.
     * This performs a blocking network request and overwrites existing cached data.
     * Call this method if hotkeys have been modified in the VTube Studio UI.
     */
    public void refresh() {
        List<Hotkey> fetchedHotkeys = this.fetchHotkeys();

        cachedHotkeys.clear();
        cachedHotkeys.putAll(fetchedHotkeys.stream().collect(
                        Collectors.toMap(Hotkey::id, hotkey -> hotkey)));
    }

    private List<Hotkey> fetchHotkeys() {
        Response response = client.sendRequest(new Request.Builder()
                .setMessageType(RequestType.HOTKEYS_IN_CURRENT_MODEL)
                .build()).join();

        JsonObject responseData = response.getData();
        return responseData.getAsJsonArray("availableHotkeys").asList().stream()
                .map(rawHotkey -> gson.fromJson(rawHotkey, Hotkey.class))
                .toList();
    }

    /**
     * Triggers the specified hotkey by sending a request to the server.
     *
     * @param hotkey The hotkey to be triggered. Cannot be null.
     */
    public void trigger(@NotNull Hotkey hotkey) {
        JsonObject payload = new JsonObject();
        payload.addProperty("hotkeyID", hotkey.id());

        this.client.sendRequest(new Request.Builder()
                .setMessageType(RequestType.HOTKEY_TRIGGER)
                .setPayload(payload)
                .build()
        );
    }

    /**
     * Triggers the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to be triggered. Cannot be null.
     */
    public void trigger(@NotNull String hotkeyName) {
        Hotkey hotkey = this.findByName(hotkeyName);
        if (hotkey == null)
            throw new NullPointerException("Hotkey not found");

        this.trigger(hotkey);
    }

    /**
     * Returns a map of hotkeys with their IDs as keys.
     *
     * @return A Map containing the hotkeys.
     */
    public Map<String, Hotkey> getHotkeys() {
        return Collections.unmodifiableMap(this.cachedHotkeys);
    }

    /**
     * Returns the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to retrieve.
     * @return The Hotkey object with the specified name, or null if not found.
     */
    @Nullable
    public Hotkey findByName(String hotkeyName) {
        return cachedHotkeys.values().stream()
                .filter(hotkey -> hotkey.name().equals(hotkeyName))
                .findFirst().orElse(null);
    }

}
