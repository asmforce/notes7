package com.asmx.data.daos;

import com.asmx.data.daos.errors.DataManagementException;

import com.asmx.data.entities.User;
import com.asmx.data.entities.UserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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
    private UserMapper userMapper = new UserMapper();

    @Override
    public boolean checkUserExists(int id) {
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject("SELECT COUNT(*) > 0 FROM users WHERE id = ?", Boolean.class, id);
        } catch (DataAccessException e) {
            logger.error("Unable to check user #" + id + " for existence");
            throw e;
        }
    }

    @Override
    public boolean checkNameInUse(String name) {
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= User.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject("SELECT COUNT(*) > 0 FROM users WHERE name = ?", Boolean.class, name);
        } catch (DataAccessException e) {
            logger.error("Unable to check user `" + name + "` for existence");
            throw e;
        }
    }

    @Override
    public int createUser(User user) {
        assert user != null;
        assert StringUtils.isNotBlank(user.getName());
        assert user.getName().length() <= User.NAME_MAX_LENGTH;
        assert StringUtils.isNotEmpty(user.getKey());
        assert user.getKey().length() <= User.KEY_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getLanguage());
        assert user.getLanguage().length() <= User.LANGUAGE_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getTimezone());
        assert user.getTimezone().length() <= User.TIMEZONE_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (id, name, key, language, timezone) VALUES (DEFAULT, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setString(1, user.getName());
                ps.setString(2, user.getKey());
                ps.setString(3, user.getLanguage());
                ps.setString(4, user.getTimezone());
                return ps;
            }, keyHolder);

            int id = keyHolder.getKey().intValue();
            user.setId(id);

            return id;
        } catch (DataAccessException e) {
            logger.error("Unable to insert a user `" + user.getName() + "`");
            throw e;
        }
    }

    @Override
    public void changeUser(User user) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(user.getName());
        assert user.getName().length() <= User.NAME_MAX_LENGTH;
        assert StringUtils.isNotEmpty(user.getKey());
        assert user.getKey().length() <= User.KEY_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getLanguage());
        assert user.getLanguage().length() <= User.LANGUAGE_MAX_LENGTH;
        assert StringUtils.isNotBlank(user.getTimezone());
        assert user.getTimezone().length() <= User.TIMEZONE_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE users SET name = ?, key = ?, language = ?, timezone = ? WHERE id = ?",
                user.getName(),
                user.getKey(),
                user.getLanguage(),
                user.getTimezone(),
                user.getId()
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            }
            if (rows < 1) {
                throw new DataManagementException("The referenced user #" + user.getId() + " does not exist");
            }
        } catch (DataAccessException e) {
            logger.error("Unable to update a user #" + user.getId());
            throw e;
        }
    }

    @Override
    public User getUser(int id) {
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<User> users = template.query("SELECT * FROM users WHERE id = ?", userMapper, id);
            if (CollectionUtils.isEmpty(users)) {
                logger.debug("A user #" + id + " not exists");
                return null;
            } else {
                if (users.size() == 1) {
                    return users.get(0);
                } else {
                    throw new DataIntegrityViolationException("A user #" + id + " duplicated " + users.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a user #" + id);
            throw e;
        }
    }

    @Override
    public User getUser(String name) {
        assert StringUtils.isNotBlank(name);

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<User> users = template.query("SELECT * FROM users WHERE name = ?", userMapper, name);
            if (CollectionUtils.isEmpty(users)) {
                logger.debug("A user `" + name + "` not exists");
                return null;
            } else {
                if (users.size() == 1) {
                    return users.get(0);
                } else {
                    throw new DataIntegrityViolationException("A user `" + name + "` duplicated " + users.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a user `" + name + "`");
            throw e;
        }
    }

    @Required
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    protected class UserMapper implements RowMapper<User> {
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
