package com.asmx.data.daos;

import com.asmx.data.entities.User;

/**
 * User: asmforce
 * Timestamp: 06.06.15 17:26.
**/
public interface UsersDao {
    User getUser(int id);
    User getUser(String name);
    boolean putUser(User user);
}
