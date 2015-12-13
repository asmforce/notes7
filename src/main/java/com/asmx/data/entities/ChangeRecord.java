package com.asmx.data.entities;

import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 29.11.15 17:28.
**/
public interface ChangeRecord {
    int getNoteId();
    void setNoteId(int noteId);
    Date getIdeaTime();
    void setIdeaTime(Date ideaTime);
    Date getChangeTime();
    void setChangeTime(Date changeTime);
}
