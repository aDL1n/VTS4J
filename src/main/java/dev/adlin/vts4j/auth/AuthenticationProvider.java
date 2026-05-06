package dev.adlin.vts4j.auth;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.PluginMeta;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProvider.class);
    private final RequestDispatcher requestDispatcher;

    public AuthenticationProvider(final @NotNull RequestDispatcher dispatcher) {
        this.requestDispatcher = dispatcher;
    }

    public CompletableFuture<String> authenticateWithNewToken(final @NotNull PluginMeta pluginMeta) {
        LOGGER.info("Requesting authenticate with new token");

        return requestToken(pluginMeta).thenCompose((token) ->
                authenticateWithExistingToken(pluginMeta, token)
                        .thenApply(response -> token)
        );
    }

    private CompletableFuture<String> requestToken(final @NotNull PluginMeta pluginMeta) {
        LOGGER.trace("Sending token request");

        final JsonObject payload = PayloadBuilder.builder()
                .addField("pluginName", pluginMeta.name())
                .addField("pluginDeveloper", pluginMeta.developer())
                .build();

        return sendAuthRequest(RequestType.AUTHENTICATION_TOKEN, payload)
                .thenApply(response -> {
                    final JsonObject responsePayload = response.payload();
                    return responsePayload.get("authenticationToken").getAsString();
                });
    }

    public CompletableFuture<Void> authenticateWithExistingToken(
            final @NotNull PluginMeta pluginMeta,
            final @NotNull String token
    ) {
        LOGGER.info("Requesting authenticate with existing token");

        final JsonObject payload = PayloadBuilder.builder()
                .addField("pluginName", pluginMeta.name())
                .addField("pluginDeveloper", pluginMeta.developer())
                .addField("authenticationToken", token)
                .build();

        return sendAuthRequest(RequestType.AUTHENTICATION, payload)
                //200iq move
                .thenAccept(response -> {});
    }

    private CompletableFuture<Response> sendAuthRequest(
            final @NotNull RequestType requestType,
            final @NotNull JsonObject payload
    ) {
        final Request authenticationRequest = RequestBuilder.of(requestType)
                .setPayload(payload)
                .build();

        LOGGER.trace("Sending authentication request: {}", authenticationRequest);
        return requestDispatcher.send(authenticationRequest);
    }
}
