package example;

import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.core.Request;
import dev.adlin.vts4j.type.EventType;
import dev.adlin.vts4j.type.RequestType;

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

        vtsClient.setEventListener((event, data) -> {
            System.out.println("Event received: " + event + " with data: " + data);
        });

        vtsClient.registerEvent(EventType.TEST);

    }
}
