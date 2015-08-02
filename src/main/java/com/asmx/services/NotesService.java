package com.asmx.services;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Space;

import java.util.List;

/**
 * A general service for notes management (spaces, chains, notes, etc.)
 *
 * User: asmforce
 * Timestamp: 22.06.15 1:10.
**/
public interface NotesService {
    List<Space> getSpaces(int userId);
    List<Space> getSpaces(int userId, Sorting sorting);
    Space getSpace(int userId, int id);
}
