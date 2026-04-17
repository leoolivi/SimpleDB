package it.leo.main.server.exceptions;

public class InvalidRowException extends RuntimeException {
    public InvalidRowException(String msg) {
        super(msg);
    }
}
