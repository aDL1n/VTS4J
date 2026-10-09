package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NonNull;

/**
 * An object used to deserialize a request into JSON format and send it to the server.
 */
public record Request(
        @SerializedName("apiName") @NonNull String apiName,
        @SerializedName("apiVersion") @NonNull String apiVersion,
        @SerializedName("requestID") @NonNull String id,
        @SerializedName("messageType") @NonNull String type,
        @SerializedName("data") @NonNull JsonObject payload
) {
}