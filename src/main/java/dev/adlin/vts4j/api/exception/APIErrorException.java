package dev.adlin.vts4j.api.exception;

public class APIErrorException extends RuntimeException {
    public APIErrorException(String message, int errorId) {
        super("Error message: " + message + " Error ID: " + errorId);
    }
}
