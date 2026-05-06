package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An object used to deserialize a request into JSON format and send it to the server.
 */
public record Request(
        @SerializedName("apiName") @NotNull String apiName,
        @SerializedName("apiVersion") @NotNull String apiVersion,
        @SerializedName("requestID") @NotNull String id,
        @SerializedName("messageType") @NotNull String type,
        @SerializedName("data") @Nullable JsonObject payload
) {
}