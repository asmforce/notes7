package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 29.11.15 17:30.
**/
public class ChangeRecordSimple implements ChangeRecord {
    private int noteId;
    private Date ideaTime;
    private Date changeTime;

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public Date getIdeaTime() {
        return ideaTime;
    }

    public void setIdeaTime(Date ideaTime) {
        this.ideaTime = ideaTime;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }
}
