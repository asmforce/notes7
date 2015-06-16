package com.asmx.controllers.errors;

/**
 * User: asmforce
 * Timestamp: 07.06.15 4:14.
**/
@SuppressWarnings("unused")
public class ForbiddenException extends Exception {
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }
}
