package dev.adlin.vts4j.exception;

public class APIErrorException extends Exception {
    public APIErrorException(String message, int errorId) {
        super("Error message: " + message + " Error ID: " + errorId);
    }
}
