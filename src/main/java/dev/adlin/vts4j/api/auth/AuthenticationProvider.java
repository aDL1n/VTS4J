package dev.adlin.vts4j.api.auth;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.api.PluginMeta;
import dev.adlin.vts4j.api.entity.Request;
import dev.adlin.vts4j.api.entity.Response;
import dev.adlin.vts4j.api.request.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AuthenticationProvider {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationProvider.class);
    private final RequestDispatcher requestDispatcher;

    public AuthenticationProvider(RequestDispatcher dispatcher) {
        this.requestDispatcher = dispatcher;
    }

    public String authenticateWithNewToken(PluginMeta pluginMeta) {
        logger.info("Requesting authenticate with new token");

        String token = requestToken(pluginMeta);
        authenticateWithExistingToken(pluginMeta, token);

        return token;
    }

    public void authenticateWithExistingToken(PluginMeta pluginMeta, String token) {
        JsonObject payload = new JsonObject();
        payload.addProperty("pluginName", pluginMeta.name());
        payload.addProperty("pluginDeveloper", pluginMeta.developer());
        payload.addProperty("authenticationToken", token);

        sendAuthRequest(
                "AuthenticationRequest",
                payload
        ).join();
    }

    private String requestToken(PluginMeta pluginMeta) {
        JsonObject payload = new JsonObject();
        payload.addProperty("pluginName", pluginMeta.name());
        payload.addProperty("pluginDeveloper", pluginMeta.developer());
        payload.addProperty("pluginIcon", "");

        Response authenticationTokenResponse = sendAuthRequest(
                "AuthenticationTokenRequest",
                payload
        ).join();

        return authenticationTokenResponse.getData()
                .get("authenticationToken").getAsString();
    }

    private CompletableFuture<Response> sendAuthRequest(String requestType, JsonObject payload) {
        logger.trace("Sending auth request: {}", requestType);

        return requestDispatcher.send(
                new Request.Builder()
                        .setMessageType(requestType)
                        .setPayload(payload)
                        .build()
        );
    }
}
