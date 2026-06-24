package dev.adlin.vts4j;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PluginMeta(@NotNull String name, @NotNull String developer, @Nullable String icon) {

    public PluginMeta(final @NotNull String name, final @NotNull String developer) {
        this(name, developer, null);
    }
}
