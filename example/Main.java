package example;

import dev.adlin.core.VTSClient;

import java.net.URI;

public class Main {
    public static void main(String[] args){
        VTSClient vts =  new VTSClient(URI.create("ws://0.0.0.0:8001"));
        vts.connect();

        vts.authenticate("Test", "Test", null).thenAccept(System.out::println);

    }
}