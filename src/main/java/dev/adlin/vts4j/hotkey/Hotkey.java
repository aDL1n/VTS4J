package dev.adlin.vts4j.hotkey;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The object needed for deserialization into JSON.
 * More about HotKeys on <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master?tab=readme-ov-file#requesting-list-of-hotkeys-available-in-current-or-other-vts-model">this page</a>
 */
public record Hotkey(
        @SerializedName("name") @NotNull String name,
        @SerializedName("type") @NotNull String type,
        @SerializedName("description") @NotNull String description,
        @SerializedName("hotkeyID") @NotNull String id,
        @SerializedName("itemInstanceID") @Nullable String itemInstanceId
) {

}
