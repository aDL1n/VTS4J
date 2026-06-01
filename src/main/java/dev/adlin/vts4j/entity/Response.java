package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * An object used to serialize the response from the server.
 */
public record Response(
        @SerializedName("apiName") @NotNull String apiName,
        @SerializedName("apiVersion") @NotNull String apiVersion,
        @SerializedName("timestamp") long timestamp,
        @SerializedName("messageType") @NotNull String requestType,
        @SerializedName("requestID") @NotNull String requestId,
        @SerializedName("data") @Nullable JsonObject payload
) {
}
