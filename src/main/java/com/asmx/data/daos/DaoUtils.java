package com.asmx.data.daos;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 13.12.15 21:03.
**/
public final class DaoUtils {
    public static Timestamp asTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return new Timestamp(date.getTime());
        }
    }

    public static Date asDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return new Date(timestamp.getTime());
        }
    }
}
