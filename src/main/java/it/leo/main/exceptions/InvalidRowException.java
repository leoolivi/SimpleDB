package it.leo.main.exceptions;

public class InvalidRowException extends RuntimeException {
    public InvalidRowException(String msg) {
        super(msg);
    }
}
