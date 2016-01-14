package com.asmx.data.daos;

import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;

/**
 * User: asmforce
 * Timestamp: 07.01.16 14:57.
**/
public interface ChainsDao {
    /**
     * Check if there is a chain entity that belongs to a user (referenced by {@link User#getId()})
     * and referenced by an {@code id}.
     * @param user a user that owns a chain.
     * @param id an id that references a chain entity.
     * @return {@code true} if a referenced chain is there or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkChainExists(User user, int id);

    /**
     * Insert a new chain entity.
     * @param user a user that will own a chain.
     * @return an id of an inserted chain.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0.
     * @throws org.springframework.dao.DataAccessException
     * if a user referenced by {@link User#getId()} doesn't exist;
     * or on database fail or data corruption.
    **/
    int createChain(User user);

    /**
     * Delete a chain entity referenced by {@code id} that belongs to a user (referenced by {@link User#getId()}).
     * @param user a user that owns a chain.
     * @param id an id that references a chain entity.
     * @return {@code true} if a referenced chain was found and deleted or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean deleteChain(User user, int id);

    /**
     * Check if a chain (referenced by an {@code id}) has a binding to a space (referenced by {@code spaceId}).
     * @param user a user that owns a chain and a space.
     * @param id an id that references a chain.
     * @param spaceId an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a binding exists or {@code false} otherwise.
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0;
     * or {@code spaceId} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean checkChainBindingExists(User user, int id, int spaceId);

    /**
     * Create a binding (if it doesn't exist) for a chain (referenced by an {@code id})
     * to a space (referenced by {@code spaceId}).
     * @param user a user that owns a chain and a space.
     * @param id an id that references a chain.
     * @param spaceId an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a binding has been created or {@code false} otherwise (if it was already there).
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0;
     * or {@code spaceId} <= 0.
     * @throws org.springframework.dao.DataAccessException
     * if a user (referenced by {@link User#getId()}) doesn't exist;
     * or a chain (referenced by {@code id}) doesn't exist;
     * or a space (referenced by {@code spaceId}) doesn't exist;
     * or on database fail or data corruption.
    **/
    boolean createChainBinding(User user, int id, int spaceId);

    /**
     * Delete a binding (if it exists) for a chain (referenced by an {@code id})
     * to a space (referenced by {@code spaceId}).
     * @param user a user that owns a chain and a space.
     * @param id an id that references a chain.
     * @param spaceId an id that references a space entity (see {@link Space#getId()}).
     * @return {@code true} if a binding has been deleted or {@code false} otherwise (if it wasn't there).
     * @throws AssertionError
     * if {@code user} is null;
     * or {@link User#getId()} <= 0;
     * or {@code id} <= 0;
     * or {@code spaceId} <= 0.
     * @throws org.springframework.dao.DataAccessException on database fail or data corruption.
    **/
    boolean deleteChainBinding(User user, int id, int spaceId);
}
