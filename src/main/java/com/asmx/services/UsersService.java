package com.asmx.services;

import com.asmx.data.entities.User;

/**
 * User: asmforce
 * Timestamp: 06.06.15 20:24.
**/
public interface UsersService {
    User getAuthorizedUser(String name, String password);
}
