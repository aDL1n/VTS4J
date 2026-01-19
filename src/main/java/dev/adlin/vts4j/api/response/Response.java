package dev.adlin.vts4j.api.response;

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
    private String messageType;
    @SerializedName("requestID")
    private String requestId;
    @SerializedName("data")
    private JsonObject data;

    public Response(String apiName, String apiVersion, long timestamp, String messageType, String requestId, JsonObject data) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.timestamp = timestamp;
        this.messageType = messageType;
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

    /**
     * @return The type of the message.
     */
    public String getMessageType() {
        return messageType;
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
        return "[apiName=%s, apiVersion=%s, timestamp=%d, messageType=%s, requestId=%s, data=%s]"
                .formatted(apiName, apiVersion, timestamp, messageType, requestId, data);
    }
}
