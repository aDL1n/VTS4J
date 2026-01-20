package example;

import ch.qos.logback.classic.Level;
import dev.adlin.vts4j.PluginMeta;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.VTSClientBuilder;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.event.EventListener;
import dev.adlin.vts4j.event.EventPriority;
import dev.adlin.vts4j.event.Listener;
import dev.adlin.vts4j.event.impl.TestEvent;
import dev.adlin.vts4j.hotkey.HotkeyManager;
import dev.adlin.vts4j.request.RequestType;

public class Main {
    public static void main(String[] args){
        VTSClient vtsClient = VTSClientBuilder.create()
                .registerListeners(new TestListener())
                .setLoggingLevel(Level.TRACE)
                .build()
                .awaitConnect();

        PluginMeta pluginMeta = new PluginMeta("Example", "test");
        vtsClient.authenticate(pluginMeta);

        vtsClient.sendRequest(
                new Request.Builder()
                        .setMessageType(RequestType.API_STATE)
                        .build()
        ).thenAccept(System.out::println);

        vtsClient.subscribe(TestEvent.class);

        HotkeyManager hotkeyManager = new HotkeyManager(vtsClient);
        hotkeyManager.refresh();

        // trigger hotkey by name
//        hotkeyManager.trigger("toggleMic");
    }

    public static class TestListener implements Listener {
        @EventListener()
        public void test1(TestEvent event) {
            System.out.println("test1 event called! " + event.counter());
        }

        @EventListener(priority = EventPriority.HIGH)
        public void test2(TestEvent event) {
            System.out.println("test2 event called! " + event.counter());
        }
    }
}