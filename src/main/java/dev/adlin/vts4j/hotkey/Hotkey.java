package dev.adlin.vts4j.hotkey;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The object needed for deserialization into JSON.
 * More about HotKeys on <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master?tab=readme-ov-file#requesting-list-of-hotkeys-available-in-current-or-other-vts-model">this page</a>
 */
public record Hotkey(
        @SerializedName("name") @NonNull String name,
        @SerializedName("type") @NonNull HotkeyAction action,
        @SerializedName("description") @NonNull String description,
        @SerializedName("hotkeyID") @NonNull String id,
        @SerializedName("itemInstanceID") @Nullable String itemInstanceId
) {
}
