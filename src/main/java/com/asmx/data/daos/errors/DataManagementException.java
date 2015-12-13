package com.asmx.data.daos.errors;

import org.springframework.dao.DataAccessException;

/**
 * User: asmforce
 * Timestamp: 29.11.15 20:36.
**/
public class DataManagementException extends DataAccessException {
    @SuppressWarnings("unused")
    public DataManagementException(String msg) {
        super(msg);
    }

    @SuppressWarnings("unused")
    public DataManagementException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
