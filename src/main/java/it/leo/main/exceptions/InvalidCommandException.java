package it.leo.main.exceptions;

public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String msg) {
        super(msg);
    }
}
