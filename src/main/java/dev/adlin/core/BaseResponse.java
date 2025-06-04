package dev.adlin.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("messageType")
    private String messageType;
    @SerializedName("requestID")
    private String requetsId;
    @SerializedName("data")
    private JsonObject data;

    public String getMessageType() {
        return messageType;
    }

    public String getRequetsId() {
        return requetsId;
    }

    public JsonObject getData() {
        return data;
    }
}
