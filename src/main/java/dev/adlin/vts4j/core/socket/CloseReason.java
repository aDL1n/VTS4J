package dev.adlin.vts4j.core.socket;

public class CloseReason {
    private int code;
    private String reason;
    private boolean remote;

    public CloseReason(int code, String reason,  boolean remote) {
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }
}