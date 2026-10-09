package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An object used to serialize the response from the server.
 */
public record Response(
        @SerializedName("apiName") @NonNull String apiName,
        @SerializedName("apiVersion") @NonNull String apiVersion,
        @SerializedName("timestamp") long timestamp,
        @SerializedName("messageType") @NonNull String requestType,
        @SerializedName("requestID") @NonNull String requestId,
        @SerializedName("data") @Nullable JsonObject payload
) {
}
