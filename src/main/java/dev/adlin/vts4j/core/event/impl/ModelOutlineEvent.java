package dev.adlin.vts4j.core.event.impl;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.Event;

public record ModelOutlineEvent(
        @SerializedName("modelName") String modelName,
        @SerializedName("modelID") String modelId,
        @SerializedName("convexHull") JsonObject convexHull,
        @SerializedName("convexHullCenter") JsonObject convexHullCenter,
        @SerializedName("windowSize") JsonObject windowSize
) implements Event {
}
