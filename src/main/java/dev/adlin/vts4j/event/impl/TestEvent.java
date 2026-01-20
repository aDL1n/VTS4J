package dev.adlin.vts4j.event.impl;

import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.event.Event;

public record TestEvent(
        @SerializedName("yourTestMessage") String testMessage,
        @SerializedName("counter") Integer counter
) implements Event {

}
