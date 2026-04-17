package it.leo.main.server.exceptions;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String msg) {
        super(msg);
    }
}
