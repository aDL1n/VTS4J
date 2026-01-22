package dev.adlin.vts4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * An object used to serialize the response from the server.
 */
public class Response {
    @SerializedName("apiName")
    private String apiName;
    @SerializedName("apiVersion")
    private String apiVersion;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("messageType")
    private String requestType;
    @SerializedName("requestID")
    private String requestId;
    @SerializedName("data")
    private JsonObject data;

    public Response(String apiName, String apiVersion, long timestamp, String requestType, String requestId, JsonObject data) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.timestamp = timestamp;
        this.requestType = requestType;
        this.requestId = requestId;
        this.data = data;
    }

    public Response() {
    }

    /**
     * @return The name of the API.
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * @return The version of the API.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @return The timestamp of the response.
     */
    public long getTimestamp() {
        return timestamp;
    }

    public String getRequestType() {
        return requestType;
    }

    /**
     * @return The unique identifier for the request.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @return The additional data associated with the response.
     */
    public JsonObject getData() {
        return data;
    }

    @Override
    public String toString() {
        return "[apiName=%s, apiVersion=%s, timestamp=%d, requestType=%s, requestId=%s, data=%s]"
                .formatted(apiName, apiVersion, timestamp, requestType, requestId, data);
    }
}
