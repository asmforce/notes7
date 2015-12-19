package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:30.
**/
public interface Tag {
    int NAME_MAX_LENGTH = 100;

    int getId();
    void setId(int id);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    Date getCreationTime();
    void setCreationTime(Date creationTime);
}
