package dev.adlin.vts4j.event.impl;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record ItemEvent(
        @SerializedName("itemEventType") String itemEventType,
        @SerializedName("itemInstanceID") String itemInstanceID,
        @SerializedName("itemFileName") String itemFileName,
        @SerializedName("itemPosition") JsonObject itemPosition
) implements Event {
}
