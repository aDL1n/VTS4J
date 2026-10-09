package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestBuilder {

    private final @NonNull RequestType requestType;

    private @NonNull String apiName = "VTubeStudioPublicAPI";
    private @NonNull String apiVersion = "1.0";
    private @NonNull String requestId = UUID.randomUUID().toString();
    private @NonNull JsonObject payload = new JsonObject();

    public static @NonNull RequestBuilder of(final @NonNull RequestType requestType) {
        return new RequestBuilder(requestType);
    }

    public static @NonNull RequestBuilder of(final @NonNull String requestTypeName) {
        return new RequestBuilder(RequestType.valueOf(requestTypeName));
    }

    /**
     * Sets the name of the API for the request.
     *
     * @param apiName The name of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NonNull RequestBuilder setApiName(final @NonNull String apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * Sets the version of the API for the request.
     *
     * @param apiVersion The version of the API. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NonNull RequestBuilder setApiVersion(final @NonNull String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * Sets the unique identifier for the request.
     *
     * @param requestId The unique identifier for the request. Cannot be null.
     * @return This Builder instance to allow method chaining.
     */
    public @NonNull RequestBuilder setRequestId(final @NonNull String requestId) {
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
    public @NonNull RequestBuilder setPayload(final @NonNull JsonObject payload) {
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
    public @NonNull Request build() {
        return new Request(
                apiName,
                apiVersion,
                requestId,
                requestType.toString(),
                payload
        );
    }
}
