package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 18.06.15 1:02.
**/
public interface SpacesDao {
    List<Space> getSpaces(User user, Sorting sorting);
    Space getSpace(User user, int id);
    boolean putSpace(User user, Space space);
}
