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
     * Check if there is a space entity that belongs to a user (referenced by {@link User#getId()})
     * and referenced by an {@code id}.
     * @param user a user that owns a space.
     * @param id an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a referenced space is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkSpaceExists(User user, int id);

    /**
     * Check if there is a space entity that belongs to a user (referenced by {@link User#getId()})
     * and referenced by a {@code name}.
     * @param user a user that owns a space.
     * @param name a name that references a space entity (see {@link Space#getName()}).
     * @return {@code true} if a referenced space is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code name} is blank;
     * or {@code name} is longer that {@link Space#NAME_MAX_LENGTH} characters.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkNameInUse(User user, String name);

    /**
     * Check if there is at least one chain bound to a space entity that belongs
     * to a user (referenced by {@link User#getId()}) and referenced by an {@code id}
     * @param user a user that owns a space.
     * @param id an id that references a space entity (see {@link Space#getId()}).
     * @return {@code false} if a referenced space is empty or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkSpaceInUse(User user, int id);

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
     * @throws org.springframework.dao.DataAccessException
     * if a user (referenced by {@link User#getId()}) doesn't exist;
     * or the name (see {@link Space#getName()}) is already in use;
     * or on database fail or data corruption.
    **/
    int createSpace(User user, Space space);

    /**
     * Update an existing space entity (referenced by an {@code id}) that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param id an id of a space to update (see {@link Space#getId()}).
     * @param name a new name of a space (see {@link Space#setName(String)}).
     * @param description a new description of a space (see {@link Space#setDescription(String)}).
     * @return {@code true} if succeeded or {@code false} if a space referenced by {@code id} doesn't exist.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0;
     * or {@code name} is blank;
     * or {@code name} is longer that {@link Space#NAME_MAX_LENGTH} characters;
     * or {@code description} is null.
     * @throws org.springframework.dao.DataAccessException
     * if the name (see {@link Space#getName()}) is already in use;
     * or on database fail or data corruption.
    **/
    boolean changeSpace(User user, int id, String name, String description);

    /**
     * Delete a space entity referenced by {@code id} that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a space.
     * @param id an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a referenced space was found and deleted or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean deleteSpace(User user, int id);

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
     * @return a referenced space entity or {@code null} (if it doesn't exist).
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    Space getSpace(User user, int id);
}
