package dev.adlin.vts4j.environment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.core.request.Request;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestWebsocketServer extends WebSocketServer {

    private final String authenticationToken = UUID.randomUUID().toString();
    private final Gson gson = new Gson();

    private final List<WebSocket> authenticatedSockets = new ArrayList<>();

    public TestWebsocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Request request = parseRequest(message);

        String requestType = request.getMessageType();
        String requestId = request.getRequestId();

        JsonObject response = null;

        switch (requestType) {
            case "AuthenticationTokenRequest" -> response = generateAuthenticationTokenResponse(requestId);
            case "AuthenticationRequest" -> {
                if (!checkAuthenticationToken(request)) return;
                response = generateAuthenticationResponse(requestId, request.getPayload());
                authenticatedSockets.add(conn);
            }
        }

        if (response != null) conn.send(gson.toJson(response));
    }

    private JsonObject generateAuthenticationResponse(String requestId, JsonObject requestPayload) {
        JsonObject data = new JsonObject();
        data.addProperty("authenticated", true);
        data.addProperty("reason", "Token valid. The plugin is authenticated for the duration of this session.");

        JsonObject response = generateResponseTemplate(requestId, "AuthenticationResponse");
        response.add("data", data);

        return response;
    }

    private JsonObject generateAuthenticationTokenResponse(String requestId) {
        JsonObject data = new JsonObject();
        data.addProperty("authenticationToken", authenticationToken.toString());

        JsonObject response = generateResponseTemplate(requestId, "AuthenticationTokenResponse");
        response.add("data", data);
        return response;
    }

    private JsonObject generateResponseTemplate(String requestId, String messageType) {
        JsonObject response = new JsonObject();
        response.addProperty("apiName", "VTubeStudioPublicAPI");
        response.addProperty("apiVersion", "1.0");
        response.addProperty("messageType", messageType);
        response.addProperty("timestamp", System.currentTimeMillis());
        response.addProperty("requestID", requestId);

        return response;
    }

    private Request parseRequest(String message) {
        return gson.fromJson(message, Request.class);
    }

    private boolean isAuthenticated(WebSocket webSocket) {
        return authenticatedSockets.contains(webSocket);
    }

    private boolean checkAuthenticationToken(Request request) {
        JsonObject payload = request.getPayload();
        String requestAuthenticationToken = payload.get("authenticationToken").getAsString();

        return authenticationToken.equals(requestAuthenticationToken);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
    }
}
