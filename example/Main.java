package example;

import com.google.gson.Gson;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.api.response.BaseResponse;

import java.net.URI;

public class Main {
    public static void main(String[] args){
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
