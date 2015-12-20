package com.asmx.data.daos;

import com.asmx.TestUtils;
import com.asmx.data.entities.User;
import com.asmx.data.entities.UserSimpleFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 05.12.15 14:51.
**/
public class UsersUtils extends TestUtils {
    private JdbcTemplate template;
    private UserSimpleFactory userFactory;
    private Set<String> uniqueNames = new HashSet<>();

    public void insert(User user) {
        final String statement = "INSERT INTO users (id, name, key, language, timezone) VALUES (?, ?, ?, ?, ?)";
        template.update(statement, user.getId(), user.getName(), user.getKey(), user.getLanguage(), user.getTimezone());
    }

    public boolean update(User user) {
        final String statement = "UPDATE users SET name = ?, key = ?, language = ?, timezone = ? WHERE id = ?";
        return template.update(statement, user.getName(), user.getKey(), user.getLanguage(), user.getTimezone(), user.getId()) > 0;
    }

    public User select(int userId) {
        try {
            final String statement = "SELECT id, name, key, language, timezone FROM users WHERE id = ?";
            return template.queryForObject(statement, (rs, index) -> {
                User user = userFactory.create();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setKey(rs.getString("key"));
                user.setLanguage(rs.getString("language"));
                user.setTimezone(rs.getString("timezone"));
                return user;
            }, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean delete(int id) {
        final String statement = "DELETE FROM users WHERE id = ?";
        return template.update(statement, id) > 0;
    }

    public User instantiate() {
        return userFactory.create();
    }

    public User generateInstance() {
        User user = userFactory.create();
        generateInstance(user);
        return user;
    }

    public User generateInstanceWithUniqueName() {
        User user = userFactory.create();
        generateInstanceWithUniqueName(user);
        return user;
    }

    public void generateInstance(User user) {
        user.setName(generateName());
        user.setKey(generateKey());
        user.setLanguage(generateLanguage());
        user.setTimezone(generateTimezone());
    }

    public void generateInstanceWithUniqueName(User user) {
        user.setName(generateUniqueName());
        user.setKey(generateKey());
        user.setLanguage(generateLanguage());
        user.setTimezone(generateTimezone());
    }

    public String generateName() {
        String name;
        do {
            name = generateString(random.nextInt(User.NAME_MAX_LENGTH) + 1);
        } while (StringUtils.isBlank(name));
        return name;
    }

    public String generateUniqueName() {
        return generateUniqueOneMore(uniqueNames, this::generateName);
    }

    public void clearGeneratedNames() {
        uniqueNames.clear();
    }

    public Set<String> getGeneratedNames() {
        return Collections.unmodifiableSet(uniqueNames);
    }

    public String generateLanguage() {
        return any("uk", "en");
    }

    public String generateTimezone() {
        return any("Europe/Kiev", "Asia/Baghdad", "America/Log_Angeles", "Asia/Shanghai");
    }

    public String generateKey() {
        return generateString(random.nextInt(User.KEY_MAX_LENGTH) + 1, true);
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Required
    public void setUserFactory(UserSimpleFactory userFactory) {
        this.userFactory = userFactory;
    }
}
