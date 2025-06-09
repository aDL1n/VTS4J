package dev.adlin.vts4j.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.VTSException;
import dev.adlin.vts4j.api.response.BaseResponse;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VTSClientTest {
    private final URI vtsUri = URI.create("ws://0.0.0.0:8002");
    private final String testToken = UUID.randomUUID().toString();

    VTSTestServer server;


    @BeforeEach
    void setupTestServer() {
        server = new VTSTestServer(new InetSocketAddress(8002));
        server.start();
    }

    @Test
    void testConnection() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        server.setMessageHandler((conn, message) -> {
            JsonObject json = new Gson().fromJson(message, JsonObject.class);
            JsonObject testAuth = testAuthResponse(json.get("requestID").getAsString(), testToken);

            future.complete(testAuth);
            conn.send(testAuth.toString());
        });

        VTSClient vts = new VTSClient(vtsUri);
        vts.connect();

        vts.authenticate("Test", "Test", null).thenAccept(jsonObject ->
                Assertions.assertEquals(jsonObject, future.join())
        );
    }

    @Test
    void testRequestIdNull() {
        VTSClient client = new VTSClient(URI.create("ws://localhost:3434"));
        Assertions.assertThrows(NullPointerException.class, () -> client.sendRequest(new JsonObject()));
    }

    @Test
    void testWebsocketNotConnected() {
        VTSClient client = new VTSClient(URI.create("ws://localhost:3434"));

        Assertions.assertThrows(WebsocketNotConnectedException.class, () -> {
            JsonObject payload = new JsonObject();
            payload.addProperty("requestID", UUID.randomUUID().toString());
            client.sendRequest(payload);
        });
    }

    @Test
    void responseTest() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        server.setMessageHandler((conn, message) -> {
            JsonObject json = new Gson().fromJson(message, JsonObject.class);
            JsonObject testAuth = testAuthResponse(json.get("requestID").getAsString(), testToken);

            future.complete(testAuth);
            conn.send(testAuth.toString());
        });

        VTSClient vts = new VTSClient(vtsUri);
        vts.connect();

        vts.authenticate("Test", "Test", null).thenAccept(jsonObject -> {
            BaseResponse response = new Gson().fromJson(jsonObject, BaseResponse.class);
            Assertions.assertEquals(response.getData(), jsonObject.get("data").getAsJsonObject());
        });
    }

//    @Test
//    void errorHandlingTest() throws ExecutionException, InterruptedException {
//        server.setMessageHandler((conn, message) -> {
//            conn.send("""
//                    {
//                    	"apiName": "VTubeStudioPublicAPI",
//                    	"apiVersion": "1.0",
//                    	"timestamp": 1625405710728,
//                    	"requestID": "SomeID",
//                    	"messageType": "APIError",
//                    	"data": {
//                    		"errorID": 50,
//                    		"message": "User has denied API access for your plugin."
//                    	}
//                    }""");
//        });
//
//        VTSClient vts = new VTSClient(vtsUri);
//        vts.connect();
//
//        CompletableFuture<JsonObject> future = vts.sendRequest(RequestBuilder.build("10", "sassa", null));
//        future.join();
//
//        Assertions.assertThrows(VTSException.class,() -> future.get());
//    }

    JsonObject testAuthResponse(@NotNull String requestId, @NotNull String token) {
        JsonObject authResponse = new JsonObject();
        authResponse.addProperty("apiName", "VTubeStudioPublicAPI");
        authResponse.addProperty("apiVersion", "1.0");
        authResponse.addProperty("timestamp", System.currentTimeMillis() / 1000);
        authResponse.addProperty("requestID", requestId);
        authResponse.addProperty("messageType", "AuthenticationTokenResponse");

        JsonObject authDataResponse = new JsonObject();
        authDataResponse.addProperty("authenticationToken", token);

        authResponse.add("data", authDataResponse);

        return authResponse;
    }
}
