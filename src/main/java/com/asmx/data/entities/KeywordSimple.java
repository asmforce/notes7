package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:39.
**/
public class KeywordSimple implements Keyword {
    private int id;
    private String name;
    private Date creationTime;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
