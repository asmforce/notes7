package com.asmx.controllers.data;

import com.asmx.Utils;
import com.asmx.data.entities.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
        HttpSession session = request.getSession(false);

        if (parameter.hasParameterAnnotation(Authorized.class)) {
            return Utils.getAuthorizedUserOrThrow(session);
        } else {
            return Utils.getAuthorizedUser(session);
        }
    }
}
