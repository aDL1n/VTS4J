package dev.adlin.vts4j.core.hotkey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.type.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages hotkeys by loading available hotkeys, triggering them, and providing access to hotkey information.
 */
public class HotkeyManager {

    private final VTSClient client;
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<String, Hotkey> availableHotkeys = new ConcurrentHashMap<>();

    public HotkeyManager(VTSClient client) {
        this.client = client;
        this.loadAvailableHotkeys();
    }

    /**
     * Loads the available hotkeys from the server and stores them in a map.
     * This method sends a request to get the hotkeys in the current model and updates the availableHotkeys map.
     */
    public void loadAvailableHotkeys() {
        Response response = client.sendRequest(new Request.Builder()
                .setMessageType(RequestType.HOTKEYS_IN_CURRENT_MODEL)
                .build()
        ).join();
        JsonObject data = response.getData();
        data.getAsJsonArray("availableHotkeys").asList()
                .stream().map(hotkeyRaw ->
                        gson.fromJson(hotkeyRaw, Hotkey.class))
                .forEach(hotkey -> availableHotkeys.put(hotkey.getId(), hotkey));
    }

    /**
     * Triggers the specified hotkey by sending a request to the server.
     *
     * @param hotkey The hotkey to be triggered. Cannot be null.
     */
    public void triggerHotkey(@NotNull Hotkey hotkey) {
        JsonObject data = new JsonObject();
        data.addProperty("hotkeyID", hotkey.getName());
        this.client.sendRequest(new Request.Builder()
                .setMessageType(RequestType.HOTKEY_TRIGGER)
                .setData(data)
                .build()
        );
    }

    /**
     * Triggers the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to be triggered. Cannot be null.
     */
    public void triggerHotkey(@NotNull String hotkeyName) {
        this.triggerHotkey(this.getHotkey(hotkeyName));
    }

    /**
     * Returns a map of available hotkeys with their IDs as keys.
     *
     * @return A ConcurrentHashMap containing the available hotkeys.
     */
    public ConcurrentHashMap<String, Hotkey> getAvailableHotkeys() {
        return availableHotkeys;
    }

    /**
     * Returns the hotkey with the specified name.
     *
     * @param hotkeyName The name of the hotkey to retrieve.
     * @return The Hotkey object with the specified name, or null if not found.
     */
    @Nullable
    public Hotkey getHotkey(String hotkeyName) {
        return availableHotkeys.values().stream()
                .filter(hotkey -> hotkey.getName().equals(hotkeyName))
                .findFirst().orElse(null);
    }

}
