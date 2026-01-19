package dev.adlin.vts4j.api.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.api.event.Event;

public record HotkeyTriggeredEvent(
        @SerializedName("hotkeyID") String hotkeyId,
        @SerializedName("hotkeyName") String hotkeyName,
        @SerializedName("hotkeyAction") String hotkeyAction,
        @SerializedName("hotkeyFile") String hotkeyFile,
        @SerializedName("hotkeyTriggeredByAPI") Boolean hotkeyTriggeredByAPI,
        @SerializedName("modelID") String modelId,
        @SerializedName("modelName") String modelName,
        @SerializedName("isLive2DItem") Boolean isLive2DItem
) implements Event {
}
