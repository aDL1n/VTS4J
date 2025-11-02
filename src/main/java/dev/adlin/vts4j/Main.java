package dev.adlin.vts4j;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        VTSClient vtsClient = new VTSClient();

        try {
            vtsClient.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(vtsClient.authenticate("Example", "test"));

        vtsClient.sendRequest(RequestBuilder.build(UUID.randomUUID().toString(), "CurrentModelRequest", null)).thenAccept(System.out::println);
    }
}
