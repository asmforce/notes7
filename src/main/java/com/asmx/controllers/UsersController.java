package com.asmx.controllers;

import com.asmx.controllers.data.entities.GenericResponseJson;
import com.asmx.controllers.data.entities.MessageJson;
import com.asmx.data.entities.User;
import com.asmx.services.UsersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 07.06.15 1:11.
**/
@Controller
@RequestMapping("/sign")
public class UsersController extends ControllerBase implements MessageSourceAware {
    @Autowired
    private UsersService usersService;
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public String sign(User user, Model model) {
        model.addAttribute("user", user);
        return "sign";
    }

    @RequestMapping(method = RequestMethod.POST, headers = AJAX_HEADER)
    @ResponseBody
    public Response signInAjax(HttpSession session, @ModelAttribute SignInForm signInForm, Locale locale) {
        String name = StringUtils.trim(signInForm.getUsername());
        String password = signInForm.getPassword();

        Response response = new Response();
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
            User user = usersService.authorize(name, password, session);
            if (user == null) {
                response.setStatusCode(GenericResponseJson.STATUS_UNAUTHORISED);
                response.addMessage(new MessageJson(
                        messageSource.getMessage("sign.unauthorized", null, locale),
                        messageSource.getMessage("sign.unauthorized.title", null, locale),
                        MessageJson.CLASS_ERROR,
                        "unauthorized"
                ));
            } else {
                response.setStatusCode(GenericResponseJson.STATUS_SUCCESS);
                response.setUsername(user.getName());
                response.setRedirection("/spaces");
            }
        } else {
            response.setStatusCode(GenericResponseJson.STATUS_FORGED_REQUEST);
            response.addMessage(new MessageJson(
                    messageSource.getMessage("error.forged_request", null, locale),
                    messageSource.getMessage("error", null, locale),
                    MessageJson.CLASS_ERROR,
                    "forged"
            ));
        }
        return response;
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
    private static class Response extends GenericResponseJson {
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
