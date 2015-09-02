package com.asmx.services;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * A general service for notes management (spaces, chains, notes, etc.)
 *
 * User: asmforce
 * Timestamp: 22.06.15 1:10.
**/
public interface NotesService {
    List<Space> getSpaces(User user);
    List<Space> getSpaces(User user, Sorting sorting);
    Space getSpace(User user, int id);
    Note getNote(User user, int id);
    List<Note> getNotes(User user, Pagination pagination);
    List<Note> getNotes(User user, Pagination pagination, Sorting sorting);
}
