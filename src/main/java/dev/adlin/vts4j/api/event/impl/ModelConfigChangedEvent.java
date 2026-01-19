package dev.adlin.vts4j.api.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.api.event.Event;

public record ModelConfigChangedEvent(
        @SerializedName("modelID") String modelId,
        @SerializedName("modelName") String modelName,
        @SerializedName("hotkeyConfigChanged") Boolean hotkeyConfigChanged
) implements Event {
}
