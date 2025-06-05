package example;

import com.google.gson.Gson;
import dev.adlin.vts4j.VTSClient;
import dev.adlin.vts4j.api.response.BaseResponse;

import java.net.URI;

public class Main {
    public static void main(String[] args){
        VTSClient vts = new VTSClient(URI.create("ws://0.0.0.0:8001"));
        vts.connect();

        vts.authenticate("Test", "Test", null).thenAccept(jsonObject ->
                System.out.println(new Gson().fromJson(jsonObject, BaseResponse.class).getData())
        );

    }
}