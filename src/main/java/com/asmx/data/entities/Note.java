package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 01.08.15 15:02.
**/
public interface Note {
    int getId();
    void setId(int id);
    int getUserId();
    void setUserId(int userId);
    int getChainId();
    void setChainId(int chainId);
    String getText();
    void setText(String text);
    Date getIdeaTime();
    void setIdeaTime(Date ideaTime);
    Date getCreationTime();
    void setCreationTime(Date creationTime);
}
