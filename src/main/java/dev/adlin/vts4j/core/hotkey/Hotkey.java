package dev.adlin.vts4j.core.hotkey;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * The object needed for deserialization into JSON.
 * More about HotKeys on <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master?tab=readme-ov-file#requesting-list-of-hotkeys-available-in-current-or-other-vts-model">this page</a>
 */
public class Hotkey {
    @SerializedName("name")
    private final String name;
    @SerializedName("type")
    private final String type;
    @SerializedName("description")
    private final String description;
    @SerializedName("hotkeyID")
    private final String id;
    @Nullable
    @SerializedName("itemInstanceID")
    private final String itemInstanceId;

    public Hotkey(String name, String type, String description, String id, @Nullable String itemInstanceId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.id = id;
        this.itemInstanceId = itemInstanceId;
    }

    /**
     * @return The name of the hotkey.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type of the hotkey.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The description of the hotkey.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The unique identifier for the hotkey.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The item instance identifier, or null if not set.
     */
    @Nullable
    public String getItemInstanceId() {
        return itemInstanceId;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", type=" + type + ", description=" + description + ", id=" + id + "]";
    }
}
