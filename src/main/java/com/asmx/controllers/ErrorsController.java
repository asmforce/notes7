package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.controllers.data.entities.GenericResponseJson;
import com.asmx.controllers.data.entities.MessageJson;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Locale;

/**
 * User: asmforce
 * Timestamp: 07.06.15 11:35.
**/
@Controller
public class ErrorsController implements MessageSourceAware {
    private MessageSource messageSource;

    @RequestMapping(value = "/403")
    public String on403(RedirectAttributes attributes, Locale locale) {
        attributes.addFlashAttribute("messages", Collections.singletonList(new MessageJson(
                messageSource.getMessage("error.unauthorized", null, locale),
                messageSource.getMessage("error", null, locale),
                MessageJson.CLASS_ERROR
        )));
        return "redirect:/sign";
    }

    @RequestMapping(value = "/403", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson onAjax403() {
        ResponseJson response = new ResponseJson(HttpStatus.FORBIDDEN);
        response.setStatusCode(GenericResponseJson.STATUS_UNAUTHORISED);
        return response;
    }

    @RequestMapping(value = "/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView on404() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping(value = "/404", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson onAjax404(Locale locale) {
        return createUnexpectedErrorResponse(HttpStatus.NOT_FOUND, locale);
    }

    @RequestMapping(value = "/415")
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ModelAndView on415() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @RequestMapping(value = "/415", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson onAjax415(Locale locale) {
        return createForgedRequestErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, locale);
    }

    @RequestMapping(value = "/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView on500() {
        return new ModelAndView("error", "code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/500", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson onAjax500(Locale locale) {
        return createUnexpectedErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, locale);
    }

    private ResponseJson createUnexpectedErrorResponse(HttpStatus status, Locale locale) {
        ResponseJson response = new ResponseJson(status);
        response.setStatusCode(GenericResponseJson.STATUS_UNEXPECTED);
        response.addMessage(new MessageJson(
                status.getReasonPhrase(),
                messageSource.getMessage("error.unexpected", null, locale),
                MessageJson.CLASS_ERROR
        ));
        return response;
    }

    private ResponseJson createForgedRequestErrorResponse(HttpStatus status, Locale locale) {
        ResponseJson response = new ResponseJson(status);
        response.setStatusCode(GenericResponseJson.STATUS_FORGED_REQUEST);
        response.addMessage(new MessageJson(
                messageSource.getMessage("error.forged_request", null, locale),
                messageSource.getMessage("error", null, locale),
                MessageJson.CLASS_ERROR,
                MessageJson.ERROR_ID_CLIENT_SERVER
        ));
        return response;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static class ResponseJson extends GenericResponseJson {
        private Integer httpStatusCode;

        public ResponseJson(HttpStatus status) {
            httpStatusCode = status.value();
        }

        @SuppressWarnings("unused")
        public Integer getHttpStatusCode() {
            return httpStatusCode;
        }
    }
}
