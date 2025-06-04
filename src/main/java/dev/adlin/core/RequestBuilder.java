package dev.adlin.core;

import com.google.gson.JsonObject;

public class RequestBuilder {

    public static JsonObject build(String requestId, String messageType, JsonObject data) {
        JsonObject payload = new JsonObject();
        payload.addProperty("apiName", "VTubeStudioPublicAPI");
        payload.addProperty("apiVersion", "1.0");
        payload.addProperty("requestID", requestId);
        payload.addProperty("messageType", messageType);
        if (data != null) payload.add("data", data);

        return payload;
    }

}
