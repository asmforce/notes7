package com.asmx.data.daos;

import com.asmx.TestUtils;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.SpaceSimpleFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 05.12.15 13:52.
**/
public class SpacesUtils extends TestUtils {
    private JdbcTemplate template;
    private SpaceSimpleFactory spaceFactory;
    private Map<Integer, Set<String>> uniqueNames = new HashMap<>();

    public void insert(Space space, int userId) {
        final String statement = "INSERT INTO spaces (id, user_id, name, description, creation_time) VALUES (?, ?, ?, ?, ?)";
        template.update(statement, space.getId(), userId, space.getName(), space.getDescription(), space.getCreationTime());
    }

    public void update(Space space, int userId) {
        final String statement = "UPDATE spaces SET user_id = ?, name = ?, description = ?, creation_time = ? WHERE id = ?";
        template.update(statement, userId, space.getName(), space.getDescription(), space.getCreationTime(), space.getId());
    }

    public Space select(int id, int userId) {
        final String statement = "SELECT * FROM spaces WHERE id = ?";
        return template.queryForObject(statement, (rs, index) -> {
            Assert.assertEquals(userId, rs.getInt("user_id"));
            Space space = spaceFactory.create();
            space.setId(rs.getInt("id"));
            space.setName(rs.getString("name"));
            space.setDescription(rs.getString("description"));
            space.setCreationTime(DaoUtils.asDate(rs.getTimestamp("creation_time")));
            return space;
        }, id);
    }

    public boolean delete(int id) {
        final String statement = "DELETE FROM spaces WHERE id = ?";
        return template.update(statement, id) > 0;
    }

    public Space generateInstance() {
        Space space = spaceFactory.create();
        generateInstance(space);
        return space;
    }

    public Space generateInstanceWithUniqueName(int userId) {
        Space space = spaceFactory.create();
        generateInstanceWithUniqueName(space, userId);
        return space;
    }

    public void generateInstance(Space space) {
        space.setName(generateName());
        space.setDescription(generateDescription());
        space.setCreationTime(new Date());
    }

    public void generateInstanceWithUniqueName(Space space, int userId) {
        space.setName(generateUniqueName(userId));
        space.setDescription(generateDescription());
        space.setCreationTime(new Date());
    }

    public String generateName() {
        String name;
        do {
            name = generateString(random.nextInt(Space.NAME_MAX_LENGTH) + 1);
        } while (StringUtils.isBlank(name));
        return name;
    }

    public String generateUniqueName(int userId) {
        Set<String> names = uniqueNames.get(userId);
        if (names == null) {
            names = new HashSet<>();
            uniqueNames.put(userId, names);
        }
        return generateUniqueOneMore(names, this::generateName);
    }

    @SuppressWarnings("unused")
    public void clearGeneratedNames(int userId) {
        uniqueNames.remove(userId);
    }

    @SuppressWarnings("unused")
    public void clearGeneratedNames() {
        uniqueNames.clear();
    }

    @SuppressWarnings("unused")
    public Set<String> getGeneratedNames(int userId) {
        Set<String> names = uniqueNames.get(userId);
        if (names == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(names);
        }
    }

    public String generateDescription() {
        // Actually an unlimited text length is allowed.
        return generateString(5000);
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Required
    public void setSpaceFactory(SpaceSimpleFactory spaceFactory) {
        this.spaceFactory = spaceFactory;
    }
}
