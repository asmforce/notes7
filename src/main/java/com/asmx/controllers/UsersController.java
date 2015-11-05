package com.asmx.controllers;

import com.asmx.Utils;
import com.asmx.controllers.data.entities.GenericResponseJson;
import com.asmx.data.entities.User;
import com.asmx.services.UsersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * User: asmforce
 * Timestamp: 07.06.15 1:11.
**/
@Controller
@RequestMapping("/sign")
public class UsersController extends ControllerBase {
    @Autowired
    private UsersService usersService;

    @Value("${timestamp.pattern}")
    private String timestampPattern;

    @RequestMapping(method = RequestMethod.GET)
    public String sign(User user, Model model) {
        model.addAttribute("user", user);
        return "sign";
    }

    @RequestMapping(method = RequestMethod.POST, headers = AJAX_HEADER)
    @ResponseBody
    public ResponseJson signInAjax(HttpSession session, @ModelAttribute SignInForm signInForm) {
        String name = StringUtils.trim(signInForm.getUsername());
        String password = signInForm.getPassword();

        return new ResponseJson() {{
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
                User user = usersService.authorize(name, password, session);
                if (user == null) {
                    setStatusCode(GenericResponseJson.STATUS_UNAUTHORISED);
                } else {
                    setStatusCode(GenericResponseJson.STATUS_SUCCESS);
                    setUsername(user.getName());
                    setTimestampPattern(Utils.getTimestampPattern(timestampPattern));
                }
            } else {
                setStatusCode(GenericResponseJson.STATUS_FORGED_REQUEST);
            }
        }};
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
    private static class ResponseJson extends GenericResponseJson {
        // Successfully authorized user's name
        private String username;
        private String timestampPattern;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getTimestampPattern() {
            return timestampPattern;
        }

        public void setTimestampPattern(String timestampPattern) {
            this.timestampPattern = timestampPattern;
        }
    }
}
