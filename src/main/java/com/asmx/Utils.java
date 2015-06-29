package com.asmx;

import com.asmx.controllers.errors.ForbiddenException;
import com.asmx.data.entities.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

/**
 * User: asmforce
 * Timestamp: 08.06.15 2:06.
**/
public final class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class);

    public static User getAuthorizedUser(HttpSession session) {
        User user = null;
        try {
            if (session == null) {
                logger.debug("session == null");
            } else {
                user = (User) session.getAttribute(Constants.AUTHORIZED_USER);
            }
        } catch (ClassCastException e) {
            logger.error("Cannot cast session attribute `" + Constants.AUTHORIZED_USER + "` to " + User.class.getName(), e);
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
