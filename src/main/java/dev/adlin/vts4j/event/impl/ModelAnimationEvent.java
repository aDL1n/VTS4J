package dev.adlin.vts4j.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record ModelAnimationEvent(
        @SerializedName("animationEventType") String animationEventType,
        @SerializedName("animationEventTime") Double animationEventTime,
        @SerializedName("animationEventData") String animationEventData,
        @SerializedName("animationName") String animationName,
        @SerializedName("animationLength") Double animationLength,
        @SerializedName("isIdleAnimation") Boolean isIdleAnimation,
        @SerializedName("modelID") String modelId,
        @SerializedName("modelName") String modelName,
        @SerializedName("isLive2DItem") Boolean isLive2DItem
) implements Event {
}
