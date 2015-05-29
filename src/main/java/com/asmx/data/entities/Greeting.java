package com.asmx.data.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * User: asmforce
 * Timestamp: 04.05.15 22:18.
 */
@JsonDeserialize(as = GreetingSimple.class)
public interface Greeting {
    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    int getValue();

    void setValue(int value);
}
