package dev.adlin.vts4j.api.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.api.event.Event;

public record Live2DCubismEditorConnectedEvent(
        @SerializedName("tryingToConnect") Boolean tryingToConnect,
        @SerializedName("connected") Boolean connected,
        @SerializedName("shouldSendParameters") Boolean shouldSendParameters
) implements Event {
}
