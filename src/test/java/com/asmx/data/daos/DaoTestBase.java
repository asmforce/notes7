package com.asmx.data.daos;

import com.asmx.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

/**
 * User: asmforce
 * Timestamp: 05.12.15 16:07.
**/
public class DaoTestBase extends TestBase {
    @Resource
    protected JdbcTemplate template;

    @Before
    @Override
    public void setUp() {
        template.update("DELETE FROM users");
    }

    protected void assertQuery(String statement, Object... args) {
        Assert.assertTrue(template.queryForObject(statement, Boolean.class, args));
    }
}
