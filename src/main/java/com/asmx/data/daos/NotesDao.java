package com.asmx.data.daos;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.entities.ChangeRecord;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 01.08.15 14:58.
**/
public interface NotesDao {
    enum RelationType {
        ANY,
        SOURCE,
        TARGET
    }

    boolean checkNoteExists(User user, int id);
    List<Note> getNotes(User user, Pagination pagination, Sorting sorting);
    List<Note> getSpaceNotes(User user, int spaceId, Pagination pagination, Sorting sorting);
    List<Note> getFreeSpaceNotes(User user, Pagination pagination, Sorting sorting);
    List<Note> getChainNotes(User user, int chainId);
    List<Note> getRelatedNotes(User user, RelationType relationType, int id, Pagination pagination, Sorting sorting);
    Note getNote(User user, int id);
    int createNote(User user, Note note);
    boolean changeNote(User user, int id, String text);
    boolean deleteNote(User user, int id);
    void createChangeRecord(User user, ChangeRecord change);
    List<ChangeRecord> getChangeRecords(User user, int id);
}
