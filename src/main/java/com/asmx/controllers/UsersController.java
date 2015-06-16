package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.Utils;
import com.asmx.controllers.data.GenericResponse;
import com.asmx.data.entities.User;
import com.asmx.services.UsersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * User: asmforce
 * Timestamp: 07.06.15 1:11.
**/
@Controller
@RequestMapping("/sign")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @RequestMapping(method = RequestMethod.GET)
    public String sign(HttpSession session, Model model) {
        model.addAttribute("user", Utils.getAuthorizedUser(session));
        return "sign";
    }

    @RequestMapping(method = RequestMethod.POST, headers = Constants.AJAX_HEADER)
    @ResponseBody
    public Response signInAjax(HttpSession session, @ModelAttribute SignInForm signInForm) {
        String name = StringUtils.trim(signInForm.getUsername());
        String password = signInForm.getPassword();

        Response response = new Response();
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
            User user = signIn(session, name, password);
            if (user == null) {
                response.setStatusCode(GenericResponse.STATUS_UNAUTHORISED);
                response.setMessages(Collections.singletonList("sign.unauthorized"));
            } else {
                response.setStatusCode(GenericResponse.STATUS_SUCCESS);
                response.setUsername(user.getName());
                response.setRedirection("/greetings");
            }
        } else {
            response.setStatusCode(GenericResponse.STATUS_INVALID_FORM);
            response.setMessages(Collections.singletonList("form.invalid"));
        }
        return response;
    }

    private User signIn(HttpSession session, String name, String password) {
        User user = usersService.getAuthorizedUser(name, password);
        session.setAttribute(Constants.AUTHORIZED_USER, user);
        return user;
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
