package com.asmx.data.daos;

import com.asmx.data.entities.User;
import com.asmx.data.entities.UserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
 * Timestamp: 06.06.15 17:31.
**/
public class UsersDaoSimple extends Dao implements UsersDao {
    private static final Logger logger = Logger.getLogger(UsersDaoSimple.class);

    private UserFactory userFactory;
    private UsersMapper usersMapper = new UsersMapper();

    @Override
    public User getUser(int id) {
        assert id >= 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<User> users = template.query("SELECT id, name, key, language, timezone FROM users WHERE id = ?", usersMapper, id);
            if (CollectionUtils.isEmpty(users)) {
                logger.debug("User #" + id + " not exists");
            } else {
                if (users.size() == 1) {
                    return users.get(0);
                } else {
                    logger.error("User #" + id + " duplicated " + users.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a user #" + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public User getUser(String name) {
        assert StringUtils.isNotEmpty(name);

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<User> users = template.query("SELECT id, name, key, language, timezone FROM users WHERE name = ?", usersMapper, name);
            if (CollectionUtils.isEmpty(users)) {
                logger.debug("User `" + name + "` not exists");
            } else {
                if (users.size() == 1) {
                    return users.get(0);
                } else {
                    logger.error("User `" + name + "` duplicated " + users.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a user `" + name + "`", e);
            throw e;
        }
        return null;
    }

    @Override
    public void putUser(User user) {
        assert user != null;
        assert user.getId() >= 0;
        assert StringUtils.isNotBlank(user.getName());
        assert user.getName().length() <= User.NAME_MAX_LENGTH;
        assert StringUtils.isNotEmpty(user.getKey());
        assert user.getKey().length() <= User.KEY_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getLanguage());
        assert user.getLanguage().length() <= User.LANGUAGE_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getTimezone());
        assert user.getTimezone().length() <= User.TIMEZONE_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        if (user.getId() == GENERATE_ID) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users (id, name, key, language, timezone) VALUES (DEFAULT, ?, ?, ?, ?)",
                        new String[] {"id"}
                );
                ps.setString(1, user.getName());
                ps.setString(2, user.getKey());
                ps.setString(3, user.getLanguage());
                ps.setString(4, user.getTimezone());
                return ps;
            }, keyHolder);

            assert rows == 1;

            Number newId = keyHolder.getKey();
            user.setId(newId.intValue());
        } else {
            template.update(
                    "UPDATE users SET name = ?, key = ?, language = ?, timezone = ? WHERE id = ?",
                    user.getName(),
                    user.getKey(),
                    user.getLanguage(),
                    user.getTimezone(),
                    user.getId()
            );
        }
    }

    @Required
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    protected class UsersMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet row, int index) throws SQLException {
            User user = userFactory.create();
            user.setId(row.getInt("id"));
            user.setName(row.getString("name"));
            user.setKey(row.getString("key"));
            user.setLanguage(row.getString("language"));
            user.setTimezone(row.getString("timezone"));
            return user;
        }
    }
}
