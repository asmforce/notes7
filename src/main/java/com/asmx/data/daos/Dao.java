package com.asmx.data.daos;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * User: asmforce
 * Timestamp: 05.05.15 23:08.
**/
@SuppressWarnings("unused")
public class Dao {
    public static final int GENERATE_ID = 0;

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        return jdbcTemplate;
    }

    protected Timestamp asTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    protected Date asDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
