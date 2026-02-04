package dev.adlin.vts4j;

import ch.qos.logback.classic.Level;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.environment.TestWebsocketServer;
import dev.adlin.vts4j.event.impl.TestEvent;
import dev.adlin.vts4j.request.RequestBuilder;
import dev.adlin.vts4j.request.RequestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

class ClientTest {

    private final TestWebsocketServer websocketServer = new TestWebsocketServer(8002);

    PluginMeta testPluginMeta = new PluginMeta("test", "test");
    private VTSClient vtsClient;

    @BeforeEach
    void setupServer() {
        websocketServer.start();

        vtsClient = createAndConnectClient();
    }

    private VTSClient createAndConnectClient() {
        final VTSClient testClient = VTSClientBuilder.create()
                .setAddress(URI.create("ws://localhost:8002"))
                .setLoggingLevel(Level.TRACE)
                .build();
        testClient.awaitConnect();

        return testClient;
    }

    @Test
    void authenticationTest() {
        final String token = vtsClient.authenticate(testPluginMeta);

        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    void testRequest() {
        Request request = RequestBuilder
                .of(RequestType.API_STATE_BROADCAST)
                .build();

        final Response response = vtsClient.sendRequest(request).join();

        Assertions.assertEquals(response.getRequestType(), request.getType());
    }

    @Test
    void testEventSubscribe() {
        vtsClient.subscribe(TestEvent.class).join();
    }
}
