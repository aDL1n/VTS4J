package dev.adlin.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public interface IResponse {
    String getRequetsId();
    String getMessageType();
    String getData();
}