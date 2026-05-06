package dev.adlin.vts4j.network;

import org.jetbrains.annotations.NotNull;

/**
 * An object for convenient work with reasons for WebSocket closure
 */
public record CloseReason(
        int code,
        @NotNull String reason,
        boolean remote
) {
}