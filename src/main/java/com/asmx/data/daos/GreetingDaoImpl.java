package com.asmx.data.daos;

import com.asmx.data.entities.Greeting;
import com.asmx.data.entities.GreetingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * User: asmforce
 * Timestamp: 04.05.15 23:00.
 */
public class GreetingDaoImpl extends BaseDao implements GreetingDao {
    protected final String TABLE_NAME = "greetings";
    protected final String ID_COLUMN = "id";
    protected final String VALUE_COLUMN = "value";
    protected final String TEXT_COLUMN = "text";

    @Autowired
    private GreetingFactory greetingFactory;
    private GreetingMapper greetingMapper = new GreetingMapper();

    @Override
    public List<Greeting> getGreetings() {
        JdbcTemplate template = getJdbcTemplate();
        return template.query("SELECT * FROM " + TABLE_NAME, greetingMapper);
    }

    @Override
    public Greeting getGreeting(int id) {
        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", greetingMapper, id);
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

        JdbcTemplate template = getJdbcTemplate();
        if (greeting.getId() > 0) {
            return template.update("UPDATE " + TABLE_NAME + " SET text = ?, value = ? WHERE id = ?", greeting.getName(), greeting.getValue(), greeting.getId()) > 0;
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO " + TABLE_NAME + " (text, value) VALUES (?, ?)",
                        new String[] {ID_COLUMN}
                );
                ps.setString(1, greeting.getName());
                ps.setInt(2, greeting.getValue());
                return ps;
            }, keyHolder);

            if (rows > 0) {
                Number newId = keyHolder.getKey();
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

    protected class GreetingMapper implements RowMapper<Greeting> {
        @Override
        public Greeting mapRow(ResultSet row, int index) throws SQLException {
            Greeting greeting = greetingFactory.create();
            greeting.setId(row.getInt(ID_COLUMN));
            greeting.setName(row.getString(TEXT_COLUMN));
            greeting.setValue(row.getInt(VALUE_COLUMN));
            return greeting;
        }
    }
}
