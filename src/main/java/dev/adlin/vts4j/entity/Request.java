package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object used to deserialize a request into JSON format and send it to the server.
 */
public class Request {
    @SerializedName("apiName")
    private final @NotNull String apiName;

    @SerializedName("apiVersion")
    private final @NotNull String apiVersion;

    @SerializedName("requestID")
    private final @NotNull String id;

    @SerializedName("messageType")
    private final @NotNull String type;

    @SerializedName("data")
    private final @Nullable JsonObject payload;

    public Request(
            final @NotNull String apiName,
            final @NotNull String apiVersion,
            final @NotNull String id,
            final @NotNull String type,
            final @Nullable JsonObject payload
    ) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public @NotNull String getApiName() {
        return apiName;
    }

    public @NotNull String getApiVersion() {
        return apiVersion;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getType() {
        return type;
    }

    public @Nullable JsonObject getPayload() {
        return payload;
    }
}