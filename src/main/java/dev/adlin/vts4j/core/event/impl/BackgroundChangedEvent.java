package dev.adlin.vts4j.core.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.Event;

public record BackgroundChangedEvent(
        @SerializedName("backgroundName") String backgroundName
) implements Event {
}
