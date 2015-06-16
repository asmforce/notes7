package com.asmx.controllers.data;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 08.06.15 0:41.
**/
@SuppressWarnings("unused")
public class GenericResponse {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_UNEXPECTED = 1;
    public static final int STATUS_UNAUTHORISED = 2;
    public static final int STATUS_INVALID_FORM = 3;
    public static final int STATUS_LAST = STATUS_INVALID_FORM;

    private Integer statusCode;
    private List<String> messages;
    private String redirection;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public String getRedirection() {
        return redirection;
    }
}
