package com.asmx;

import com.asmx.data.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 08.06.15 2:06.
**/
public final class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class);

    public static User getAuthorizedUser(HttpSession session) {
        return Utils.getSessionAttribute(User.class, session, Constants.AUTHORIZED_USER);
    }

    public static Locale getLocale(HttpServletRequest request) {
        Locale locale = Utils.getSessionAttribute(Locale.class, request.getSession(false), Constants.AUTHORIZED_USER_LOCALE);
        if (locale == null) {
            locale = request.getLocale();
        }
        return locale;
    }

    public static Locale getLocale(HttpSession session) {
        Locale locale = Utils.getSessionAttribute(Locale.class, session, Constants.AUTHORIZED_USER_LOCALE);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static Locale getLocale(User user) {
        return Locale.forLanguageTag(user.getLanguage());
    }

    public static String getTimestampPattern(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            try {
                new SimpleDateFormat(pattern);
                return pattern;
            } catch (IllegalArgumentException e) {
                logger.error("Configured invalid timestamp pattern: `" + pattern + "`, falling back to the default one");
            }
        }
        return Constants.DEFAULT_TIMESTAMP_PATTERN;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getSessionAttribute(Class<T> cls, HttpSession session, String attribute) {
        T value = null;
        try {
            if (session == null) {
                logger.debug("session == null");
            } else {
                value = cls.cast(session.getAttribute(attribute));
            }
        } catch (ClassCastException e) {
            logger.error("Cannot cast session attribute `" + attribute + "`, type is unexpected", e);
        }
        return value;
    }
}
