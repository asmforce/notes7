package com.asmx.controllers.data;

import com.asmx.Utils;
import com.asmx.controllers.errors.ForbiddenException;
import com.asmx.data.entities.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * User: asmforce
 * Timestamp: 23.06.15 22:47.
**/
public class AuthorizedUserResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        User user = Utils.getAuthorizedUser(request.getSession(false));
        if (user == null && parameter.hasParameterAnnotation(Authorized.class)) {
            throw new ForbiddenException("User authorization required");
        }
        return user;
    }
}
