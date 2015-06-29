package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 18.06.15 0:54.
**/
public interface Space {
    int NAME_MAX_LENGTH = 100;

    int getId();
    void setId(int id);
    int getUserId();
    void setUserId(int userId);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    Date getCreationTime();
    void setCreationTime(Date creationTime);
}
