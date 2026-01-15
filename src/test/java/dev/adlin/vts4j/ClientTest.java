package dev.adlin.vts4j;

import dev.adlin.vts4j.core.request.Request;
import dev.adlin.vts4j.core.request.RequestType;
import dev.adlin.vts4j.environment.TestWebsocketServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientTest {

    private TestWebsocketServer websocketServer = new TestWebsocketServer(8001);

    PluginMeta testPluginMeta = new PluginMeta("test", "test");

    @BeforeEach
    public void setupServer() {
        websocketServer.start();
    }

    public VTSClient createAndConnectClient() {
        VTSClient client = new VTSClient();
        try {
            client.connectBlocking();

            return client;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void connectionTest() throws InterruptedException {
        VTSClient client = new VTSClient();

        client.connectBlocking();
    }

    @Test
    public void authenticationTest() {
        VTSClient client = createAndConnectClient();

        client.authenticate(testPluginMeta);
    }

    @Test
    public void testRequest() {
        VTSClient client = createAndConnectClient();

        Request request = new Request.Builder()
                .setMessageType(RequestType.API_STATE_BROADCAST)
                .build();

        client.sendRequest(request).join();
    }

    @Test
    public void testEventSubscribe() {
        VTSClient client = createAndConnectClient();

        client.subscribeToEvent("TestEvent").join();
    }
}
