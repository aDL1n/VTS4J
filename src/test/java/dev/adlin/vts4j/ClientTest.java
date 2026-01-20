package dev.adlin.vts4j;

import dev.adlin.vts4j.api.PluginMeta;
import dev.adlin.vts4j.api.VTSClient;
import dev.adlin.vts4j.api.VTSClientBuilder;
import dev.adlin.vts4j.api.event.impl.TestEvent;
import dev.adlin.vts4j.api.entity.Request;
import dev.adlin.vts4j.api.request.RequestType;
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
        VTSClient client = VTSClientBuilder.create().build();
        client.awaitConnect();

        return client;
    }

    @Test
    public void connectionTest() {
        VTSClient client = VTSClientBuilder.create().build();

        client.awaitConnect();
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

        client.subscribe(TestEvent.class).join();
    }
}
