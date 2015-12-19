package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:39.
**/
public interface Keyword {
    int NAME_MAX_LENGTH = 100;

    int getId();
    void setId(int id);
    String getName();
    void setName(String name);
    Date getCreationTime();
    void setCreationTime(Date creationTime);
}
