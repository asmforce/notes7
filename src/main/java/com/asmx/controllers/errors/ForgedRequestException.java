package com.asmx.controllers.errors;

/**
 * User: asmforce
 * Timestamp: 01.09.15 0:05.
**/
@SuppressWarnings("unused")
public class ForgedRequestException extends Exception {
    public ForgedRequestException() {
        super();
    }

    public ForgedRequestException(String message) {
        super(message);
    }

    public ForgedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForgedRequestException(Throwable cause) {
        super(cause);
    }
}
