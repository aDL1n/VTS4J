package dev.adlin.vts4j.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

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

    public String getApiName() {
        return apiName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getRequestId() {
        return requestId;
    }

    public JsonObject getData() {
        return data;
    }

    @Override
    public String toString() {
        return "[apiName=%s, apiVersion=%s, timestamp=%d, messageType=%s, requestId=%s, data=%s]"
                .formatted(apiName, apiVersion, timestamp, messageType, requestId, data);
    }
}
