package com.asmx.data.daos;

import com.asmx.data.entities.User;

/**
 * User: asmforce
 * Timestamp: 06.06.15 17:26.
**/
public interface UsersDao {
    /**
     * Check if there is a user referenced by an {@code id}.
     * @param id a user's id to check.
     * @return {@code true} if a user is there or {@code false} otherwise.
     * @throws AssertionError if {@code id <= 0}.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkUserExists(int id);

    /**
     * Check if there is a user referenced by a {@code name}.
     * @param name a user's name to check.
     * @return {@code true} if a user is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code name} is null or blank;
     * or {@code name} is longer than {@link User#NAME_MAX_LENGTH} characters.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkNameInUse(String name);

    /**
     * Insert a new user entity. Allocate a new id and return it via {@code user} object (see {@link User#setId(int)}).
     * @param user a user entity to insert.
     * @return an id of an inserted user.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getName()} is null or blank;
     * or {@link User#getName()} is longer than {@link User#NAME_MAX_LENGTH} characters;
     * or {@link User#getKey()} is empty;
     * or {@link User#getKey()} is longer that {@link User#KEY_MAX_LENGTH} characters;
     * or {@link User#getLanguage()} is null or blank;
     * or {@link User#getLanguage()} is longer than {@link User#LANGUAGE_MAX_LENGTH} characters;
     * or {@link User#getTimezone()} is null or blank;
     * or {@link User#getTimezone()} is longer than {@link User#TIMEZONE_MAX_LENGTH} characters.
     * @throws org.springframework.dao.DataAccessException
     * if a user's name is already in use;
     * or on database fail or data corruption.
    **/
    int createUser(User user);

    /**
     * Update an existing user entity (referenced by {@link User#getId()}).
     * @param user a user entity to store.
     * @return {@code true} if succeeded or {@code false} if a user referenced by {@link User#getId()} doesn't exist.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@link User#getName()} is null or blank;
     * or {@link User#getName()} is longer than {@link User#NAME_MAX_LENGTH} characters;
     * or {@link User#getKey()} is empty;
     * or {@link User#getKey()} is longer that {@link User#KEY_MAX_LENGTH} characters;
     * or {@link User#getLanguage()} is null or blank;
     * or {@link User#getLanguage()} is longer than {@link User#LANGUAGE_MAX_LENGTH} characters;
     * or {@link User#getTimezone()} is null or blank;
     * or {@link User#getTimezone()} is longer than {@link User#TIMEZONE_MAX_LENGTH} characters.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean changeUser(User user);

    /**
     * Retrieve a user entity referenced by {@code id}.
     * @param id an id that references a user entity (see {@link User#getId()}).
     * @return a referenced user entity or null (if it doesn't exist).
     * @throws AssertionError if {@code id <= 0}.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    User getUser(int id);

    /**
     * Retrieve a user entity referenced by {@code name}.
     * @param name a name that references a user entity (see {@link User#getName()}.
     * @return a referenced user entity or null (if it doesn't exist).
     * @throws AssertionError if {@code name} is blank.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    User getUser(String name);
}
