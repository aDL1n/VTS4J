package dev.adlin.vts4j.request;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A fluent builder utility designed for constructing GSON {@link JsonObject} instances.
 * <p>
 * This class provides a typesafe, chainable API to easily append properties and
 * handle nullable nested JSON objects without boilerplate checks.
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * JsonObject payload = PayloadBuilder.builder()
 *         .addField("pluginName", "MyPlugin")
 *         .addField("enabled", true)
 *         .addField("version", 1)
 *         .addOfNullable("config", optionalConfigJson)
 *         .build();
 * }</pre>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PayloadBuilder {

    /**
     * The underlying JSON object instance being accumulated.
     */
    private final JsonObject json = new JsonObject();

    /**
     * Creates a new fluent instance of {@code PayloadBuilder}.
     *
     * @return a fresh builder instance
     */
    public static @NonNull PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    /**
     * Appends a text property to the JSON object.
     *
     * @param field the JSON property key name
     * @param value the string value to assign
     * @return this builder instance for method chaining
     */
    public @NonNull PayloadBuilder addField(
            final @NonNull String field,
            final @NonNull String value
    ) {
        json.addProperty(field, value);
        return this;
    }

    /**
     * Appends a character property to the JSON object.
     *
     * @param field the JSON property key name
     * @param value the character value to assign
     * @return this builder instance for method chaining
     */
    public @NonNull PayloadBuilder addField(
            final @NonNull String field,
            final @NonNull Character value
    ) {
        json.addProperty(field, value);
        return this;
    }

    /**
     * Appends a numeric property (Integer, Double, Long, etc.) to the JSON object.
     *
     * @param field the JSON property key name
     * @param value the numeric value to assign
     * @return this builder instance for method chaining
     */
    public @NonNull PayloadBuilder addField(
            final @NonNull String field,
            final @NonNull Number value
    ) {
        json.addProperty(field, value);
        return this;
    }

    /**
     * Appends a boolean property to the JSON object.
     *
     * @param field the JSON property key name
     * @param value the boolean value to assign
     * @return this builder instance for method chaining
     */
    public @NonNull PayloadBuilder addField(
            final @NonNull String field,
            final @NonNull Boolean value
    ) {
        json.addProperty(field, value);
        return this;
    }

    /**
     * Conditionally appends a nested {@link JsonObject} if the provided value is not null.
     *
     * @param field the JSON property key name for the nested object
     * @param value the nullable JSON object payload
     * @return this builder instance for method chaining
     */
    public @NonNull PayloadBuilder addOfNullable(
            final @NonNull String field,
            final @Nullable JsonObject value
    ) {
        if (value != null) {
            json.add(field, value);
        }
        return this;
    }

    /**
     * Concludes the construction process and returns the fully assembled JSON structure.
     *
     * @return the constructed {@link JsonObject} instance
     */
    public @NonNull JsonObject build() {
        return json;
    }
}
