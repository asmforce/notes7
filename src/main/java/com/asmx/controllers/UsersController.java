package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.controllers.data.entities.GenericResponse;
import com.asmx.controllers.data.entities.Message;
import com.asmx.data.entities.User;
import com.asmx.services.UsersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 07.06.15 1:11.
**/
@Controller
@RequestMapping("/sign")
public class UsersController implements MessageSourceAware {
    @Autowired
    private UsersService usersService;
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView sign(User user) {
        return new ModelAndView("sign", "user", user);
    }

    @RequestMapping(method = RequestMethod.POST, headers = Constants.AJAX_HEADER)
    @ResponseBody
    public Response signInAjax(HttpSession session, @ModelAttribute SignInForm signInForm, Locale locale) {
        String name = StringUtils.trim(signInForm.getUsername());
        String password = signInForm.getPassword();

        Response response = new Response();
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
            User user = signIn(session, name, password);
            if (user == null) {
                response.setStatusCode(GenericResponse.STATUS_UNAUTHORISED);
                response.addMessage(new Message(
                        messageSource.getMessage("sign.unauthorized", null, locale),
                        messageSource.getMessage("sign.unauthorized.title", null, locale),
                        Message.CLASS_ERROR,
                        "unauthorized"
                ));
            } else {
                response.setStatusCode(GenericResponse.STATUS_SUCCESS);
                response.setUsername(user.getName());
                response.setRedirection("/spaces");
            }
        } else {
            response.setStatusCode(GenericResponse.STATUS_INVALID_FORM);
            response.addMessage(new Message(
                    messageSource.getMessage("form.invalid", null, locale),
                    messageSource.getMessage("error", null, locale),
                    Message.CLASS_ERROR,
                    "invalid"
            ));
        }
        return response;
    }

    private User signIn(HttpSession session, String name, String password) {
        User user = usersService.getAuthorizedUser(name, password);
        session.setAttribute(Constants.AUTHORIZED_USER, user);
        return user;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @SuppressWarnings("unused")
    public static class SignInForm {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @SuppressWarnings("unused")
    private static class Response extends GenericResponse {
        // Successfully authorized user's name
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
