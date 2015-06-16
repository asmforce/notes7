package com.asmx.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * User: asmforce
 * Timestamp: 07.06.15 11:35.
**/
@Controller
public class ErrorsController {
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String onGet403() {
        return "redirect:/sign";
    }

    @RequestMapping(value = "/403", produces = "application/json")
    @ResponseBody
    public ErrorData on403() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ErrorData(status.getReasonPhrase(), status.value());
    }

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView onGet500() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/500")
    @ResponseBody
    public ErrorData on500() {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ErrorData(status.getReasonPhrase(), status.value());
    }

    @SuppressWarnings("unused")
    protected static class ErrorData {
        private String message;
        private Integer code;

        public ErrorData(String message, Integer code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public Integer getCode() {
            return code;
        }
    }
}
