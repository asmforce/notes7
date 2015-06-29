package com.asmx.controllers.data;

import com.asmx.Utils;
import com.asmx.data.entities.User;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 27.06.15 19:52.
**/
public class UserLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        User user = Utils.getAuthorizedUser(request.getSession(false));
        Locale locale = null;

        if (user != null) {
            locale = Locale.forLanguageTag(user.getLanguage());
        }

        return locale == null ? request.getLocale() : locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }
}
