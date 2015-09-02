package com.asmx.data.daos;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 01.08.15 14:58.
**/
public interface NotesDao {
    List<Note> getNotes(User user, Pagination pagination, Sorting sorting);
    List<Note> getSpaceNotes(User user, int spaceId, Pagination pagination, Sorting sorting);
    List<Note> getFreeSpaceNotes(User user, Pagination pagination, Sorting sorting);
    List<Note> getChainNotes(User user, int chainId);
    Note getNote(User user, int id);
    boolean putNote(User user, Note note);
}
