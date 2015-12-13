package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 29.11.15 15:11.
**/
public interface Attachment {
    int getId();
    void setId(int id);
    String getText();
    void setText(String text);
    String getComment();
    void setComment(String comment);
    Date getTime();
    void setTime(Date time);
}
