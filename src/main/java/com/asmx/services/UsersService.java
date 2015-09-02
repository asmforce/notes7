package com.asmx.services;

import com.asmx.data.entities.User;

import javax.servlet.http.HttpSession;

/**
 * User: asmforce
 * Timestamp: 06.06.15 20:24.
**/
public interface UsersService {
    User authorize(String name, String password, HttpSession session);
}
