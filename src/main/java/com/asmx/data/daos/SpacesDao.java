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
    /**
     * Check if there is a space identified by an {@code id} that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param id an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkSpaceExists(User user, int id);

    /**
     * Check if there is a space identified by a {@code name} that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param name a name that references a space entity (see {@link Space#getName()}).
     * @return {@code true} if a is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code name} is blank;
     * or {@code name} is longer that {@link Space#NAME_MAX_LENGTH} characters.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkNameInUse(User user, String name);

    /**
     * Insert a new space entity. Allocate a new id and return in via {@code space} object (see {@link Space#setId(int)}).
     * @param user a user that will own a space.
     * @param space a space entity to store.
     * @return an id of an inserted space.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code space} is null;
     * or {@link Space#getName()} is blank;
     * or {@link Space#getName()} is longer that {@link Space#NAME_MAX_LENGTH} characters;
     * or {@link Space#getDescription()} is null;
     * or {@link Space#getCreationTime()} is null.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    int createSpace(User user, Space space);

    /**
     * Update an existing space entity (referenced by an {@code id}) that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param id an id of a space to update (see {@link Space#getId()}).
     * @param name a new name of a space (see {@link Space#setName(String)}).
     * @param description a new description of a space (see {@link Space#setDescription(String)}).
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0;
     * or {@code name} is blank;
     * or {@code name} is longer that {@link Space#NAME_MAX_LENGTH} characters;
     * or {@code description} is null.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    void changeSpace(User user, int id, String name, String description);

    /**
     * Retrieve all space entities that belong to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param sorting a sorting parameters for retrieved entities or null (to use a default sorting).
     * @return a list or retrieved entities or empty list.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    List<Space> getSpaces(User user, Sorting sorting);

    /**
     * Retrieve a space entity referenced by {@code id} that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param id an id that references a space entity (see {@link Space#getId()}).
     * @return a referenced space entity of null (if it doesn't exist).
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    Space getSpace(User user, int id);
}
