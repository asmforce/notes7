package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Note;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 01.08.15 14:58.
**/
public interface NotesDao {
    List<Note> getNotes(int userId, Sorting sorting);
    List<Note> getSpaceNotes(int userId, int spaceId, Sorting sorting);
    List<Note> getFreeSpaceNotes(int userId, Sorting sorting);
    List<Note> getChainNotes(int userId, int chainId);
    Note getNote(int userId, int id);
    boolean putNote(Note note);
}
