package dev.adlin.vts4j.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record PostProcessingEvent(
        @SerializedName("currentOnState") Boolean currentOnState,
        @SerializedName("currentPreset") String currentPreset
) implements Event {
}
