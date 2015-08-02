package com.asmx.controllers.data.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * User: asmforce
 * Timestamp: 08.06.15 0:41.
**/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponseJson {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_UNEXPECTED = 1;
    public static final int STATUS_UNAUTHORISED = 2;
    public static final int STATUS_FORGED_REQUEST = 3;
    public static final int STATUS_LAST = STATUS_FORGED_REQUEST;

    private Integer statusCode;
    private List<MessageJson> messages;
    private String redirection;

    @SuppressWarnings("unused")
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SuppressWarnings("unused")
    public List<MessageJson> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageJson> messages) {
        this.messages = messages;
    }

    public void addMessage(MessageJson message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }

    @SuppressWarnings("unused")
    public String getRedirection() {
        return redirection;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }
}
