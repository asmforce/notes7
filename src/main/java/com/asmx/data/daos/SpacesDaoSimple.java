package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.SpaceFactory;
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
    public List<Space> getSpaces(int userId, Sorting sorting) {
        assert userId >= 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                    "SELECT * FROM spaces s WHERE user_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                    spaceMapper, userId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get spaces (user #" + userId + ")");
            throw e;
        }
    }

    @Override
    public Space getSpace(int userId, int id) {
        assert userId > 0;
        assert id >= 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Space> spaces = template.query(
                    "SELECT * FROM spaces WHERE user_id = ? AND id = ?",
                    spaceMapper, userId, id
            );

            if (CollectionUtils.isEmpty(spaces)) {
                logger.debug("A space #" + id + " (user #" + userId + ") not exists");
            } else {
                if (spaces.size() == 1) {
                    return spaces.get(0);
                } else {
                    throw new DataIntegrityViolationException("A space #" + id + " (user #" + userId + ") duplicated " + spaces.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a space #" + id + " (user #" + userId + ")");
            throw e;
        }
        return null;
    }

    @Override
    public boolean putSpace(Space space) {
        assert space.getId() >= 0;
        assert space.getUserId() > 0;
        assert StringUtils.isNotBlank(space.getName());
        assert space.getName().length() < Space.NAME_MAX_LENGTH;
        assert space.getDescription() != null;
        assert space.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        if (space.getId() == GENERATE_ID) {
            try {
                GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
                template.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO spaces (id, user_id, name, description, creation_time) VALUES (DEFAULT, ?, ?, ?, ?)",
                            new String[]{"id"}
                    );
                    ps.setInt(1, space.getUserId());
                    ps.setString(2, space.getName());
                    ps.setString(3, space.getDescription());
                    ps.setTimestamp(4, asTimestamp(space.getCreationTime()));
                    return ps;
                }, keyHolder);

                Number newId = keyHolder.getKey();
                space.setId(newId.intValue());
                return true;
            } catch (DataAccessException e) {
                logger.error("Unable to insert space (user #" + space.getUserId() + ")");
                throw e;
            }
        } else {
            try {
                int rows = template.update(
                        "UPDATE spaces SET name = ?, description = ?, creation_time = ? WHERE id = ? AND user_id = ?",
                        space.getName(),
                        space.getDescription(),
                        space.getCreationTime(),
                        space.getId(),
                        space.getUserId()
                );

                return rows >= 1;
            } catch (DataAccessException e) {
                logger.error("Unable to update space #" + space.getId() + " (user #" + space.getUserId() + ")");
                throw e;
            }
        }
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
            space.setUserId(row.getInt("user_id"));
            space.setName(row.getString("name"));
            space.setDescription(row.getString("description"));
            space.setCreationTime(asDate(row.getTimestamp("creation_time")));
            return space;
        }
    }
}
