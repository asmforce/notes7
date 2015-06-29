package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.controllers.data.entities.GenericResponse;
import com.asmx.controllers.data.entities.Message;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 07.06.15 11:35.
**/
@Controller
public class ErrorsController implements MessageSourceAware {
    private MessageSource messageSource;

    @RequestMapping(value = "/403")
    public String on403() {
        return "redirect:/sign";
    }

    @RequestMapping(value = "/403", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax403(Locale locale) {
        return createResponse(HttpStatus.FORBIDDEN, locale);
    }

    @RequestMapping(value = "/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView on404() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping(value = "/404", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax404(Locale locale) {
        return createResponse(HttpStatus.NOT_FOUND, locale);
    }

    @RequestMapping(value = "/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView on500() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/500", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public Response onAjax500(Locale locale) {
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, locale);
    }

    private Response createResponse(HttpStatus status, Locale locale) {
        Response response = new Response(status);
        response.addMessage(new Message(
                status.getReasonPhrase(),
                messageSource.getMessage("error.unexpected", null, locale),
                Message.CLASS_ERROR,
                Integer.toString(status.value())
        ));
        return response;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static class Response extends GenericResponse {
        private Integer httpStatusCode;

        public Response(HttpStatus status) {
            setStatusCode(STATUS_UNEXPECTED);
            httpStatusCode = status.value();
        }

        @SuppressWarnings("unused")
        public Integer getHttpStatusCode() {
            return httpStatusCode;
        }
    }
}
