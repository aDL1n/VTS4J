package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RequestBuilder {
    private final @NotNull String requestType;
    private @NotNull String apiName = "VTubeStudioPublicAPI";
    private @NotNull String apiVersion = "1.0";
    private @Nullable String requestId;
    private @Nullable JsonObject payload;

    private RequestBuilder(String type) {
        this.requestType = type;
    }

    public static RequestBuilder of(RequestType requestType) {
        return new RequestBuilder(requestType.toString());
    }

    public static RequestBuilder of(String requestTypeName) {
        return new RequestBuilder(requestTypeName);
    }

    /**
     * Sets the name of the API for the request.
     *
     * @param apiName The name of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public RequestBuilder setApiName(final @NotNull String apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * Sets the version of the API for the request.
     *
     * @param apiVersion The version of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public RequestBuilder setApiVersion(final @NotNull String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * Sets the unique identifier for the request.
     *
     * @param requestId The unique identifier for the request. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public RequestBuilder setRequestId(final @NotNull String requestId) {
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
    public RequestBuilder setPayload(final @NotNull JsonObject payload) {
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
        return new Request(
                apiName,
                apiVersion,
                requestId == null ? UUID.randomUUID().toString() : requestId,
                requestType,
                payload
        );
    }
}
