package dev.adlin.vts4j.core.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.Event;

public record ModelLoadedEvent(
        @SerializedName("modelLoaded") Boolean modelLoaded,
        @SerializedName("modelName") String modelName,
        @SerializedName("modelID") String modelId
) implements Event {

}
