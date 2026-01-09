package dev.adlin.vts4j.core.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.Event;

public record Live2DCubismEditorConnectedEvent(
        @SerializedName("tryingToConnect") Boolean tryingToConnect,
        @SerializedName("connected") Boolean connected,
        @SerializedName("shouldSendParameters") Boolean shouldSendParameters
) implements Event {
}
