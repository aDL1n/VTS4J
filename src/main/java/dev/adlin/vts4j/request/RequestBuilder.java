package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RequestBuilder {

    private final @NotNull RequestType requestType;

    private @NotNull String apiName = "VTubeStudioPublicAPI";
    private @NotNull String apiVersion = "1.0";
    private @NotNull String requestId = UUID.randomUUID().toString();
    private @NotNull JsonObject payload = new JsonObject();

    private RequestBuilder(final @NotNull RequestType type) {
        this.requestType = type;
    }

    public static @NotNull RequestBuilder of(final @NotNull RequestType requestType) {
        return new RequestBuilder(requestType);
    }

    public static @NotNull RequestBuilder of(final @NotNull String requestTypeName) {
        return new RequestBuilder(RequestType.valueOf(requestTypeName));
    }

    /**
     * Sets the name of the API for the request.
     *
     * @param apiName The name of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NotNull RequestBuilder setApiName(final @NotNull String apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * Sets the version of the API for the request.
     *
     * @param apiVersion The version of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NotNull RequestBuilder setApiVersion(final @NotNull String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * Sets the unique identifier for the request.
     *
     * @param requestId The unique identifier for the request. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NotNull RequestBuilder setRequestId(final @NotNull String requestId) {
        this.requestId = requestId;
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
    public @NotNull RequestBuilder setPayload(final @NotNull JsonObject payload) {
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
    public @NotNull Request build() {
        return new Request(
                apiName,
                apiVersion,
                requestId,
                requestType.toString(),
                payload
        );
    }
}
