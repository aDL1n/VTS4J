package dev.adlin.vts4j.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record TrackingStatusChangedEvent(
        @SerializedName("faceFound") Boolean faceFound,
        @SerializedName("leftHandFound") Boolean leftHandFound,
        @SerializedName("rightHandFound") Boolean rightHandFound
) implements Event {
}
