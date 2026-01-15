package example;

import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.core.hotkey.HotkeyManager;
import dev.adlin.vts4j.type.EventType;
import dev.adlin.vts4j.type.RequestType;
import dev.adlin.vts4j.core.event.impl.TestEvent;

public class Main {
    public static void main(String[] args){
        VTSClient vtsClient = new VTSClient();

        try {
            vtsClient.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        vtsClient.authenticate("Example", "test");

        vtsClient.sendRequest(
                new Request.Builder()
                        .setMessageType(RequestType.API_STATE)
                        .build()
        ).thenAccept(System.out::println);

        vtsClient.registerEventListener(new TestListener());

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