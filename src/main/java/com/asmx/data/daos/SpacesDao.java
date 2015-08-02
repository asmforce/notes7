package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Space;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 18.06.15 1:02.
**/
public interface SpacesDao {
    List<Space> getSpaces(int userId, Sorting sorting);
    Space getSpace(int userId, int id);
    boolean putSpace(Space space);
}
