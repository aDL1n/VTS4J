package dev.adlin.vts4j.core.event.data;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.core.event.EventData;

public record TestEventData(
        @SerializedName("yourTestMessage") String testMessage,
        @SerializedName("counter") Integer counter
) implements EventData {

}
