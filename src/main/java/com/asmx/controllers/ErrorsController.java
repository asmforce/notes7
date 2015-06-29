package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.controllers.data.entities.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * User: asmforce
 * Timestamp: 07.06.15 11:35.
**/
@Controller
public class ErrorsController {
    @RequestMapping(value = "/403")
    public String on403() {
        return "redirect:/sign";
    }

    @RequestMapping(value = "/403", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax403() {
        return new Response(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView on404() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping(value = "/404", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax404() {
        return new Response(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView on500() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/500", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax500() {
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static class Response extends GenericResponse {
        private Integer httpStatusCode;

        public Response(HttpStatus status) {
            setStatusCode(STATUS_UNEXPECTED);
            setMessages(Collections.singletonList(status.getReasonPhrase()));
            httpStatusCode = status.value();
        }

        @SuppressWarnings("unused")
        public Integer getHttpStatusCode() {
            return httpStatusCode;
        }
    }
}
