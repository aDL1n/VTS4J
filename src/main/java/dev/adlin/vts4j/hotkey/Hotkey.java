package dev.adlin.vts4j.hotkey;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The object needed for deserialization into JSON.
 * More about HotKeys on <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master?tab=readme-ov-file#requesting-list-of-hotkeys-available-in-current-or-other-vts-model">this page</a>
 */
public record Hotkey(
        @SerializedName("name") String name,
        @SerializedName("type") String type,
        @SerializedName("description") String description,
        @SerializedName("hotkeyID") String id,
        @SerializedName("itemInstanceID") @Nullable String itemInstanceId
) {

    /**
     * @return The name of the hotkey.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * @return The type of the hotkey.
     */
    @Override
    public String type() {
        return type;
    }

    /**
     * @return The description of the hotkey.
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * @return The unique identifier for the hotkey.
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * @return The item instance identifier, or null if not set.
     */
    @Override
    @Nullable
    public String itemInstanceId() {
        return itemInstanceId;
    }

    @Override
    @NotNull
    public String toString() {
        return "[name=" + name + ", type=" + type + ", description=" + description + ", id=" + id + "]";
    }
}
