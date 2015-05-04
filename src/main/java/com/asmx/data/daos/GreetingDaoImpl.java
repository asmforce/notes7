package com.asmx.data.daos;

import com.asmx.data.entities.Greeting;
import com.asmx.data.entities.GreetingFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * User: asmforce
 * Timestamp: 04.05.15 23:00.
 */
public class GreetingDaoImpl implements GreetingDao {
    protected final String TABLE_NAME = "greetings";
    protected final String ID_COLUMN = "id";
    protected final String VALUE_COLUMN = "value";
    protected final String TEXT_COLUMN = "text";

    private GreetingFactory greetingFactory;
    private GreetingMapper greetingMapper = new GreetingMapper();
    private JdbcTemplate template;

    @Override
    public List<Greeting> getGreetings() {
        return template.query("SELECT * FROM " + TABLE_NAME, greetingMapper);
    }

    @Override
    public Greeting getGreeting(int id) {
        try {
            return template.queryForObject("SELECT * FROM " + TABLE_NAME, greetingMapper);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean putGreeting(Greeting greeting) {
        if (greeting == null) {
            throw new IllegalArgumentException("greeting == null");
        }

        if (greeting.getId() > 0) {
            return template.update("UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?", greeting.getName(), greeting.getId()) > 0;
        } else {
            PreparedStatementCreatorFactory statementCreatorFactory = new PreparedStatementCreatorFactory("INSERT INTO " + TABLE_NAME + " (text) VALUES (?)");
            PreparedStatementCreator statementCreator = statementCreatorFactory.newPreparedStatementCreator(new Object[]{
                    greeting.getName()
            });
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            if (template.update(statementCreator, generatedKeyHolder) > 0) {
                Number newId = generatedKeyHolder.getKey();
                greeting.setId(newId.intValue());
                return true;
            } else {
                return false;
            }
        }
    }

    @Required
    public void setGreetingFactory(GreetingFactory greetingFactory) {
        this.greetingFactory = greetingFactory;
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    private class GreetingMapper implements RowMapper<Greeting> {
        @Override
        public Greeting mapRow(ResultSet row, int index) throws SQLException {
            Greeting greeting = greetingFactory.create();
            greeting.setId(row.getInt(ID_COLUMN));
            greeting.setName(row.getString(TEXT_COLUMN));
            return greeting;
        }
    }
}
