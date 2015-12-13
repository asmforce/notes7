package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 29.11.15 15:11.
**/
public class AttachmentSimple implements Attachment {
    private int id;
    private String text;
    private String comment;
    private Date time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
