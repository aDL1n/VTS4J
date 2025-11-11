package dev.adlin.vts4j.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.type.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Request {
    @NotNull
    @SerializedName("apiName")
    private final String apiName;
    @NotNull
    @SerializedName("apiVersion")
    private final String apiVersion;
    @NotNull
    @SerializedName("requestID")
    private final String requestId;
    @NotNull
    @SerializedName("messageType")
    private final String messageType;
    @Nullable
    @SerializedName("data")
    private final JsonObject data;

    private Request(@NotNull String apiName,@NotNull String apiVersion,@NotNull String requestId,@NotNull String messageType,@Nullable JsonObject data) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.requestId = requestId;
        this.messageType = messageType;
        this.data = data;
    }

    public @NotNull String getApiName() {
        return apiName;
    }

    public @NotNull String getApiVersion() {
        return apiVersion;
    }

    public @NotNull String getRequestId() {
        return requestId;
    }

    public @NotNull String getMessageType() {
        return messageType;
    }

    public @Nullable JsonObject getData() {
        return data;
    }

    public static class Builder {
        @Nullable
        private String apiName;
        @Nullable
        private String apiVersion;
        @Nullable
        private String requestId;
        @NotNull
        private String messageType;
        @Nullable
        private JsonObject data;

        public Builder setApiName(@NotNull String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder setApiVersion(@NotNull String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public Builder setRequestId(@NotNull String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setMessageType(@NotNull RequestType type) {
            this.messageType = type.getRequestName();
            return this;
        }

        public Builder setMessageType(@NotNull String type) {
            this.messageType = type;
            return this;
        }

        public Builder setData(@NotNull JsonObject data) {
            this.data = data;
            return this;
        }

        public Request build() {
            if (messageType == null) throw new IllegalArgumentException("messageType cannot be null");

            return new Request(
                    apiName == null ? "VTubeStudioPublicAPI" : apiName,
                    apiVersion ==  null ? "1.0" : apiVersion,
                    requestId == null ? UUID.randomUUID().toString() : requestId,
                    messageType,
                    data
            );
        }
    }
}