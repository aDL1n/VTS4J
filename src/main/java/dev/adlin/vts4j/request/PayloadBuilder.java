package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayloadBuilder {

    private final JsonObject json = new JsonObject();

    public static @NotNull PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public @NotNull PayloadBuilder addField(
            final @NotNull String field,
            final @NotNull String value
    ) {
        json.addProperty(field, value);
        return this;
    }

    public @NotNull PayloadBuilder addField(
            final @NotNull String field,
            final @NotNull Character value
    ) {
        json.addProperty(field, value);
        return this;
    }

    public @NotNull PayloadBuilder addField(
            final @NotNull String field,
            final @NotNull Number value
    ) {
        json.addProperty(field, value);
        return this;
    }

    public @NotNull PayloadBuilder addField(
            final @NotNull String field,
            final @NotNull Boolean value
    ) {
        json.addProperty(field, value);
        return this;
    }

    public @NotNull PayloadBuilder addOfNullable(
            final @NotNull String field,
            final @Nullable JsonObject value
    ) {
        if (value != null) json.add(field, value);
        return this;
    }

    public @NotNull JsonObject build() {
        return json;
    }
}
