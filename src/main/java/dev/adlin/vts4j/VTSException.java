package dev.adlin.vts4j;

public class VTSException extends Exception{

    public VTSException(String exception) {
        super("VTubeStudio API error: " + exception);
    }

    public VTSException(Throwable throwable) {
        super(throwable);
    }

    public VTSException() {
    }

}
