package dev.adlin.vts4j.core.event.impl;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.Event;

public record ModelMovedEvent(
        @SerializedName("modelID") String modelId,
        @SerializedName("modelName") String modelName,
        @SerializedName("modelPosition") JsonObject modelPosition
) implements Event {
}
