package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayloadBuilder {

    private final JsonObject json = new JsonObject();

    public static @NotNull PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public @NotNull PayloadBuilder addField(final @NotNull String field, final @NotNull String value) {
        json.add(field, new JsonPrimitive(value));
        return this;
    }

    public @NotNull PayloadBuilder addField(final @NotNull String field, final @NotNull Character value) {
        json.add(field, new JsonPrimitive(value));
        return this;
    }

    public @NotNull PayloadBuilder addField(final @NotNull String field, final @NotNull Number value) {
        json.add(field, new JsonPrimitive(value));
        return this;
    }

    public @NotNull PayloadBuilder addField(final @NotNull String field, final @NotNull Boolean value) {
        json.add(field, new JsonPrimitive(value));
        return this;
    }

    public @NotNull JsonObject build() {
        return json;
    }
}
