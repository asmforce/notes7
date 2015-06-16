package com.asmx;

import com.asmx.controllers.errors.ForbiddenException;
import com.asmx.data.entities.User;

import javax.servlet.http.HttpSession;

/**
 * User: asmforce
 * Timestamp: 08.06.15 2:06.
**/
public final class Utils {
    public static User getAuthorizedUser(HttpSession session) {
        User user = null;
        try {
            user = (User) session.getAttribute(Constants.AUTHORIZED_USER);
        } catch (ClassCastException e) {
            // Nothing to do
        }
        return user;
    }

    public static User getAuthorizedUserOrThrow(HttpSession session) throws ForbiddenException {
        User user = getAuthorizedUser(session);
        if (user == null) {
            throw new ForbiddenException("User authorization required");
        }
        return user;
    }
}
