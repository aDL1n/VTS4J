package dev.adlin.vts4j;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An immutable data record representing metadata configuration for a plugin.
 * <p>
 * This metadata is required by the VTube Studio API during the initial handshake
 * and token authentication processes to identify the application.
 * </p>
 *
 * @param name      the unique, human-readable name of the plugin
 * @param developer the name of the organization or developer who created the plugin
 * @param icon      the optional Base64-encoded string or identifier of the plugin's icon,
 *                  can be {@code null} if not provided
 */
public record PluginMeta(
        @NonNull String name,
        @NonNull String developer,
        @Nullable String icon
) {

    /**
     * Overloaded constructor to initialize plugin metadata without an icon.
     *
     * @param name      the unique, human-readable name of the plugin
     * @param developer the name of the organization or developer who created the plugin
     */
    public PluginMeta(final @NonNull String name, final @NonNull String developer) {
        this(name, developer, null);
    }
}
