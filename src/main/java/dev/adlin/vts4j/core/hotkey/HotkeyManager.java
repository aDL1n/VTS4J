package dev.adlin.vts4j.core.hotkey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.core.Response;
import dev.adlin.vts4j.type.RequestType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class HotkeyManager {

    private final VTSClient client;
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<String, Hotkey> availableHotkeys = new ConcurrentHashMap<>();

    public HotkeyManager(VTSClient client) {
        this.client = client;
        this.loadAvailableHotkeys();
    }

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

    public void triggerHotkey(Hotkey hotkey) {
        JsonObject data = new JsonObject();
        data.addProperty("hotkeyID", hotkey.getName());

        this.client.sendRequest(new Request.Builder()
                .setMessageType(RequestType.HOTKEY_TRIGGER)
                .setData(data)
                .build()
        );
    }

    public void triggerHotkey(String hotkeyName) {
        this.triggerHotkey(this.getHotkey(hotkeyName));
    }

    public ConcurrentHashMap<String, Hotkey> getAvailableHotkeys() {
        return availableHotkeys;
    }

    @Nullable
    public Hotkey getHotkey(String hotkeyName) {
        return availableHotkeys.values().stream()
                .filter(hotkey -> hotkey.getName().equals(hotkeyName))
                .findFirst().orElse(null);
    }


}
