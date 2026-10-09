package dev.adlin.vts4j.authentication;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.PluginMeta;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.request.PayloadBuilder;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestDispatcher;
import dev.adlin.vts4j.request.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Authentication provider responsible for managing token requests.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationProvider {

    private final @NonNull RequestDispatcher requestDispatcher;

    /**
     * Performs a full authentication workflow for a plugin by generating a new token.
     * <p>
     * This method first requests a new authentication token and subsequently
     * uses it to verify the plugin's authenticity.
     * </p>
     *
     * @param pluginMeta the metadata of the plugin being authenticated
     * @return a {@link CompletableFuture} that resolves to the successfully issued and verified token string
     */
    public @NonNull CompletableFuture<String> authenticateWithNewToken(final @NonNull PluginMeta pluginMeta) {
        log.info("Starting authentication with a new token for plugin '{}'", pluginMeta.name());

        return requestToken(pluginMeta).thenCompose(token ->
                authenticateWithExistingToken(pluginMeta, token)
                        .thenApply(__ -> {
                            log.info("Successfully authenticated plugin '{}' with new token", pluginMeta.name());
                            return token;
                        }));
    }

    /**
     * Requests a new authentication token for the plugin.
     *
     * @param pluginMeta the metadata of the plugin requesting the token
     * @return a {@link CompletableFuture} containing the token string extracted from the server response
     */
    private @NonNull CompletableFuture<String> requestToken(final @NonNull PluginMeta pluginMeta) {
        log.debug("Requesting authentication token for plugin '{}'", pluginMeta.name());

        final JsonObject payload = PayloadBuilder.builder()
                .addField("pluginName", pluginMeta.name())
                .addField("pluginDeveloper", pluginMeta.developer())
                .build();

        return sendAuthRequest(RequestType.AUTHENTICATION_TOKEN, payload)
                .thenApply(this::extractTokenFromResponse)
                .thenApply(token -> {
                    log.trace("Successfully retrieved token for plugin '{}'", pluginMeta.name());
                    return token;
                });
    }

    /**
     * Extracts the authentication token string from the received response payload.
     *
     * @param response the server response
     * @return the raw authentication token string
     */
    private @NonNull String extractTokenFromResponse(final @NonNull Response response) {
        final JsonObject responsePayload = response.payload();
        return responsePayload.get("authenticationToken").getAsString();
    }

    /**
     * Authenticates a plugin using an already existing token.
     *
     * @param pluginMeta the metadata of the plugin being authenticated
     * @param token      the existing authentication token string
     * @return a {@link CompletableFuture} that completes when the authentication request succeeds
     */
    public @NonNull CompletableFuture<Void> authenticateWithExistingToken(
            final @NonNull PluginMeta pluginMeta,
            final @NonNull String token
    ) {
        log.debug("Authenticating with existing token for plugin '{}'", pluginMeta.name());

        final JsonObject payload = PayloadBuilder.builder()
                .addField("pluginName", pluginMeta.name())
                .addField("pluginDeveloper", pluginMeta.developer())
                .addField("authenticationToken", token)
                .build();

        return sendAuthRequest(RequestType.AUTHENTICATION, payload)
                .thenRun(() -> log.debug("Token authentication request acknowledged for plugin '{}'", pluginMeta.name()));
    }

    /**
     * Constructs and dispatches an asynchronous authentication request via the request dispatcher.
     *
     * @param requestType the type of the request being performed
     * @param payload     the JSON payload of the request
     * @return a {@link CompletableFuture} containing the raw server response
     */
    private @NonNull CompletableFuture<Response> sendAuthRequest(
            final @NonNull RequestType requestType,
            final @NonNull JsonObject payload
    ) {
        final Request authenticationRequest = RequestBuilder.of(requestType)
                .setPayload(payload)
                .build();

        log.trace("Sending request [{}] with payload: {}", requestType, payload);
        return requestDispatcher.send(authenticationRequest);
    }
}
