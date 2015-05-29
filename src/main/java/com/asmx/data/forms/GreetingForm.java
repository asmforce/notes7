package com.asmx.data.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: asmforce
 * Timestamp: 07.05.15 0:29.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GreetingForm {
    private String message;
    private int id;

    public GreetingForm() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
