package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;

public class PayloadBuilder {

    private final Map<String, JsonPrimitive> fields = new HashMap<>();

    private PayloadBuilder() {
    }

    public static PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public PayloadBuilder addField(String field, String value) {
        fields.putIfAbsent(field, new JsonPrimitive(value));
        return this;
    }

    public PayloadBuilder addField(String field, Character value) {
        fields.putIfAbsent(field, new JsonPrimitive(value));
        return this;
    }

    public PayloadBuilder addField(String field, Number value) {
        fields.putIfAbsent(field, new JsonPrimitive(value));
        return this;
    }

    public PayloadBuilder addField(String field, Boolean value) {
        fields.putIfAbsent(field, new JsonPrimitive(value));
        return this;
    }

    public JsonObject build() {
        final JsonObject json = new JsonObject();

        fields.forEach(json::add);

        return json;
    }
}
