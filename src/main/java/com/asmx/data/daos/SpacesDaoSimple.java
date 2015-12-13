package com.asmx.data.daos;

import com.asmx.data.Sorting;

import com.asmx.data.daos.errors.DataManagementException;

import com.asmx.data.entities.Space;
import com.asmx.data.entities.SpaceFactory;
import com.asmx.data.entities.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: asmforce
 * Timestamp: 18.06.15 1:11.
**/
public class SpacesDaoSimple extends Dao implements SpacesDao {
    private static final Logger logger = Logger.getLogger(SpacesDaoSimple.class);

    private static final Sorting DEFAULT_SORTING = Sorting.sorted("id", true);

    private SpaceFactory spaceFactory;
    private SpaceMapper spaceMapper = new SpaceMapper();

    @Override
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<String, String>() {{
            put("id", "s.id");
            put("name", "s.name");
            put("description", "s.description");
            put("creation_time", "s.creation_time");
        }};
    }

    @Override
    public boolean checkSpaceExists(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM spaces WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check space #" + id + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkNameInUse(User user, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Space.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM spaces WHERE user_id = ? AND name = ?",
                Boolean.class,
                user.getId(), name
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check space `" + name + "` for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createSpace(User user, Space space) {
        assert user != null;
        assert user.getId() > 0;
        assert space != null;
        assert StringUtils.isNotBlank(space.getName());
        assert StringUtils.length(space.getName()) <= Space.NAME_MAX_LENGTH;
        assert space.getDescription() != null;
        assert space.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spaces (id, user_id, name, description, creation_time) VALUES (DEFAULT, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                ps.setString(2, space.getName());
                ps.setString(3, space.getDescription());
                ps.setTimestamp(4, asTimestamp(space.getCreationTime()));
                return ps;
            }, keyHolder);

            int id = keyHolder.getKey().intValue();
            space.setId(id);

            return id;
        } catch (DataAccessException e) {
            logger.error("Unable to create space (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void changeSpace(User user, int id, String name, String description) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Space.NAME_MAX_LENGTH;
        assert description != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE spaces SET name = ?, description = ? " +
                "WHERE user_id = ? AND id = ?",
                name, description, user.getId(), id
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            }
            if (rows < 1) {
                throw new DataManagementException("The referenced space does not exist");
            }
        } catch (DataAccessException e) {
            logger.error("Unable to update space #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Space> getSpaces(User user, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM spaces s WHERE user_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                spaceMapper, user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get spaces (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Space getSpace(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Space> spaces = template.query(
                "SELECT * FROM spaces WHERE user_id = ? AND id = ?",
                spaceMapper, user.getId(), id
            );

            if (CollectionUtils.isEmpty(spaces)) {
                logger.debug("A space #" + id + " (user #" + user.getId() + ") not exists");
            } else {
                if (spaces.size() == 1) {
                    return spaces.get(0);
                } else {
                    throw new DataIntegrityViolationException("A space #" + id + " (user #" + user.getId() + ") duplicated " + spaces.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a space #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Required
    public void setSpaceFactory(SpaceFactory spaceFactory) {
        this.spaceFactory = spaceFactory;
    }

    protected class SpaceMapper implements RowMapper<Space> {
        @Override
        public Space mapRow(ResultSet row, int index) throws SQLException {
            Space space = spaceFactory.create();
            space.setId(row.getInt("id"));
            space.setName(row.getString("name"));
            space.setDescription(row.getString("description"));
            space.setCreationTime(asDate(row.getTimestamp("creation_time")));
            return space;
        }
    }
}
