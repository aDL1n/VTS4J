package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


/**
 * An object used to deserialize a request into JSON format and send it to the server.
 */
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
    private final JsonObject payload;

    private Request(@NotNull String apiName,@NotNull String apiVersion,@NotNull String requestId,@NotNull String messageType,@Nullable JsonObject payload) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.requestId = requestId;
        this.messageType = messageType;
        this.payload = payload;
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

    public @Nullable JsonObject getPayload() {
        return payload;
    }


    /**
     * Builder for simplified Request creation.
     */
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
        private JsonObject payload;

        /**
         * Sets the name of the API for the request.
         *
         * @param apiName The name of the API. Cannot be null.
         * @return This Builder instance to allow method chaining.
         */
        public Builder setApiName(@NotNull String apiName) {
            this.apiName = apiName;
            return this;
        }

        /**
         * Sets the version of the API for the request.
         *
         * @param apiVersion The version of the API. Cannot be null.
         * @return This Builder instance to allow method chaining.
         */
        public Builder setApiVersion(@NotNull String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        /**
         * Sets the unique identifier for the request.
         *
         * @param requestId The unique identifier for the request. Cannot be null.
         * @return This Builder instance to allow method chaining.
         */
        public Builder setRequestId(@NotNull String requestId) {
            this.requestId = requestId;
            return this;
        }

        /**
         * Sets the type of the message for the request using a RequestType enum.
         *
         * @param type The type of the message as a RequestType enum. Cannot be null.
         * @return This Builder instance to allow method chaining.
         */
        public Builder setMessageType(@NotNull RequestType type) {
            this.messageType = type.getRequestName();
            return this;
        }

        /**
         * Sets the type of the message for the request using a String.
         *
         * @param type The type of the message as a String. Cannot be null.
         * @return This Builder instance to allow method chaining.
         */
        public Builder setMessageType(@NotNull String type) {
            this.messageType = type;
            return this;
        }

        /**
         *  Sets payload required for certain types of requests.
         *  More details about the payload structure can be found at
         *  <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master?tab=readme-ov-file#api-details">this page</a>.
         *
         * @param payload Additional information required for certain types of requests
         * @return This Builder instance to allow method chaining
         */
        public Builder setPayload(@NotNull JsonObject payload) {
            this.payload = payload;
            return this;
        }

        /**
         * Builds and returns a new Request object with the configured parameters.
         * If messageType is not set, an IllegalArgumentException is thrown.
         *
         * @return A new Request object with the configured parameters.
         * @throws IllegalArgumentException If messageType is null.
         */
        public Request build() {
            if (messageType == null) throw new IllegalArgumentException("messageType cannot be null");

            return new Request(
                    apiName == null ? "VTubeStudioPublicAPI" : apiName,
                    apiVersion ==  null ? "1.0" : apiVersion,
                    requestId == null ? UUID.randomUUID().toString() : requestId,
                    messageType,
                    payload
            );
        }
    }
}