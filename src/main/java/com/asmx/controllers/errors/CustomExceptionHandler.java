package com.asmx.controllers.errors;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * User: asmforce
 * Timestamp: 07.06.15 13:38.
**/
@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = Logger.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler()
    public String onForgedRequest(ForgedRequestException e) {
        logger.error("Forged request error", e);
        return "forward:/" + HttpServletResponse.SC_BAD_REQUEST;
    }

    @ExceptionHandler(ForbiddenException.class)
    public String onForbidden(ForbiddenException e) {
        logger.debug("User authorization required", e);
        return "forward:/" + HttpServletResponse.SC_FORBIDDEN;
    }

    @ExceptionHandler(NotFoundException.class)
    public String onNotFoundException(NotFoundException e) {
        logger.error("Request leads to nowhere", e);
        return "forward:/" + HttpServletResponse.SC_NOT_FOUND;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public String onDataAccessException(HttpMediaTypeNotSupportedException e) {
        logger.error("Unsupported media type", e);
        return "forward:/" + HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
    }

    @ExceptionHandler(DataAccessException.class)
    public String onDataAccessException(DataAccessException e) {
        logger.error("Database error", e);
        return "forward:/" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
