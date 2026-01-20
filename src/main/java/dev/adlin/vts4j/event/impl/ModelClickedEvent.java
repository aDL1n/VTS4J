package dev.adlin.vts4j.event.impl;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record ModelClickedEvent(
        @SerializedName("modelLoaded") Boolean modelLoaded,
        @SerializedName("loadedModelID") String loadedModelId,
        @SerializedName("loadedModelName") String loadedModelName,
        @SerializedName("modelWasClicked") Boolean modelWasClicked,
        @SerializedName("mouseButtonID") Integer mouseButtonId,
        @SerializedName("clickPosition") JsonObject clickPosition,
        @SerializedName("windowSize") JsonObject windowSize,
        @SerializedName("clickedArtMeshCount") Integer clickedArtMeshCount,
        @SerializedName("artMeshHits") JsonObject artMeshHits
) implements Event {
}
