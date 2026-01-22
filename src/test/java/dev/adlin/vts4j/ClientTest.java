package dev.adlin.vts4j;

import ch.qos.logback.classic.Level;
import dev.adlin.vts4j.event.impl.TestEvent;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestType;
import dev.adlin.vts4j.environment.TestWebsocketServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class ClientTest {

    private TestWebsocketServer websocketServer = new TestWebsocketServer(8002);

    PluginMeta testPluginMeta = new PluginMeta("test", "test");
    private VTSClient client;

    @BeforeEach
    public void setupServer() {
        websocketServer.start();

        client = createAndConnectClient();
    }

    public VTSClient createAndConnectClient() {
        VTSClient client = VTSClientBuilder.create()
                .setAddress(URI.create("ws://localhost:8002"))
                .setLoggingLevel(Level.TRACE)
                .build();
        client.awaitConnect();

        return client;
    }

    @Test
    public void authenticationTest() {
        client.authenticate(testPluginMeta);
    }

    @Test
    public void testRequest() {
        Request request = RequestBuilder
                .of(RequestType.API_STATE_BROADCAST)
                .build();

        client.sendRequest(request).join();
    }

    @Test
    public void testEventSubscribe() {
        client.subscribe(TestEvent.class).join();
    }
}
