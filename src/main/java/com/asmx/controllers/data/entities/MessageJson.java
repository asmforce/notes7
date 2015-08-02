package com.asmx.controllers.data.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * User: asmforce
 * Timestamp: 29.06.15 20:20.
**/
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class MessageJson {
    public static final String CLASS_INFO = "info";
    public static final String CLASS_WARNING = "warning";
    public static final String CLASS_ERROR = "negative";

    public static final String ERROR_ID_CLIENT_SERVER = "client-server";

    private String title;
    private String message;
    private String classes;
    private String id;

    public MessageJson() {
    }

    public MessageJson(String message) {
        this.message = message;
    }

    public MessageJson(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public MessageJson(String message, String title, String classes) {
        this.message = message;
        this.title = title;
        this.classes = classes;
    }

    public MessageJson(String message, String title, String classes, String id) {
        this.message = message;
        this.title = title;
        this.classes = classes;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
