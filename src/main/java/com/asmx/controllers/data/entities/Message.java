package com.asmx.controllers.data.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * User: asmforce
 * Timestamp: 29.06.15 20:20.
**/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    public static final String CLASS_INFO = "info";
    public static final String CLASS_WARNING = "warning";
    public static final String CLASS_ERROR = "negative";

    private String title;
    private String message;
    private String classes;
    private String id;

    public Message() {
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public Message(String message, String title, String classes) {
        this.message = message;
        this.title = title;
        this.classes = classes;
    }

    public Message(String message, String title, String classes, String id) {
        this.message = message;
        this.title = title;
        this.classes = classes;
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
