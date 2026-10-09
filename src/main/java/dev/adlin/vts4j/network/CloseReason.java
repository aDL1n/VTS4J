package dev.adlin.vts4j.network;

import org.jspecify.annotations.NonNull;

/**
 * An object for convenient work with reasons for WebSocket closure
 */
public record CloseReason(
        int code,
        @NonNull String reason,
        boolean remote
) {
}