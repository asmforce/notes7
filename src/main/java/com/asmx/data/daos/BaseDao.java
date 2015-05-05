package com.asmx.data.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * User: asmforce
 * Timestamp: 05.05.15 23:08.
 */
@Component
public class BaseDao {
    @Autowired
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
}
