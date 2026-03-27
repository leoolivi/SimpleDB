package it.leo.main.exceptions;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String msg) {
        super(msg);
    }
}
