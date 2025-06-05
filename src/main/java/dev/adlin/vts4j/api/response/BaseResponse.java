package dev.adlin.vts4j.api.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.adlin.vts4j.api.response.impl.IResponse;

public class BaseResponse implements IResponse {
    @SerializedName("messageType")
    private String messageType;
    @SerializedName("requestID")
    private String requestId;
    @SerializedName("data")
    private JsonObject data;

    public String getMessageType() {
        return messageType;
    }

    public String getRequestId() {
        return requestId;
    }

    public JsonObject getData() {
        return data;
    }
}
