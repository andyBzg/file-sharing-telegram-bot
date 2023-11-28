package com.andybzg.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }

    public InvalidEmailException(Throwable cause) {
        super(cause);
    }

    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
