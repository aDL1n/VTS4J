package dev.adlin.vts4j.api.network;

/**
 * An object for convenient work with reasons for WebSocket closure
 */
public class CloseReason {
    private int code;
    private String reason;
    private boolean remote;

    public CloseReason(int code, String reason,  boolean remote) {
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    /**
     * @return connection close code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return reason for closing the connection
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return bool indicating whether it was called by the server or the client
     */
    public boolean isRemote() {
        return remote;
    }
}