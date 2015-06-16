package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 06.06.15 17:55.
**/
public interface User {
    int NAME_MAX_LENGTH = 50;
    int KEY_MAX_LENGTH = 256;
    int LANGUAGE_MAX_LENGTH = 2;
    int TIMEZONE_MAX_LENGTH = 50;

    int getId();
    void setId(int id);
    String getName();
    void setName(String name);
    String getKey();
    void setKey(String key);
    String getLanguage();
    void setLanguage(String language);
    String getTimezone();
    void setTimezone(String timezone);
}
