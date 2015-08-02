package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 01.08.15 15:02.
**/
public class NoteSimple implements Note {
    private int id;
    private int userId;
    private int chainId;
    private String text;
    private Date ideaTime;
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
    public int getUserId() {
        return userId;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int getChainId() {
        return chainId;
    }

    @Override
    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getIdeaTime() {
        return ideaTime;
    }

    @Override
    public void setIdeaTime(Date ideaTime) {
        this.ideaTime = ideaTime;
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
