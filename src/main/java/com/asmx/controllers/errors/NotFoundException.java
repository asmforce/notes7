package com.asmx.controllers.errors;

/**
 * User: asmforce
 * Timestamp: 22.06.15 2:00.
**/
@SuppressWarnings("unused")
public class NotFoundException extends Exception {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
