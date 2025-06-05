package dev.adlin.vts4j.api.response.impl;

import com.google.gson.JsonObject;

public interface IResponse {
    String getRequestId();
    String getMessageType();
    JsonObject getData();
}
