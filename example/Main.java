package example;

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
                        .setMessageType("CurrentModelRequest")
                        .build()
        ).thenAccept(System.out::println);
    }
}
