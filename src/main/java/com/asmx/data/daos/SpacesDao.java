package com.asmx.data.daos;

import com.asmx.data.entities.Space;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 18.06.15 1:02.
**/
public interface SpacesDao {
    List<Space> getSpaces(int userId);
    Space getSpace(int userId, int id);
    Space getSpace(int id);
    Space getSpace(int userId, String name);
    void putSpace(Space space);
}
