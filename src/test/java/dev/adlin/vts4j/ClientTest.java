package dev.adlin.vts4j;

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
}
