package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Tag;
import com.asmx.data.entities.TagFactory;
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
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:58.
**/
public class TagsDaoSimple extends Dao implements TagsDao {
    private static final Logger logger = Logger.getLogger(TagsDaoSimple.class);

    private static final Sorting DEFAULT_SORTING = Sorting.sorted("name", true);

    private TagFactory tagFactory;
    private TagMapper tagMapper = new TagMapper();

    @Override
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<String, String>() {{
            put("id", "t.id");
            put("name", "t.name");
            put("description", "t.description");
            put("creation_time", "t.creation_time");
        }};
    }

    @Override
    public boolean checkTagExists(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM tags WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check tag #" + id + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkNameInUse(User user, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Tag.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM tags WHERE user_id = ? AND name = ?",
                Boolean.class,
                user.getId(), name
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check tag `" + name + "` for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkTagInUse(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM tag_bindings WHERE user_id = ? AND tag_id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check if tag #" + id + " is in use (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createTag(User user, Tag tag) {
        assert user != null;
        assert user.getId() > 0;
        assert tag != null;
        assert StringUtils.isNotBlank(tag.getName());
        assert StringUtils.length(tag.getName()) <= Tag.NAME_MAX_LENGTH;
        assert tag.getDescription() != null;
        assert tag.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tags (id, user_id, name, description, creation_time) VALUES (DEFAULT, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                ps.setString(2, tag.getName());
                ps.setString(3, tag.getDescription());
                ps.setTimestamp(4, DaoUtils.asTimestamp(tag.getCreationTime()));
                return ps;
            }, keyHolder);

            int id = keyHolder.getKey().intValue();
            tag.setId(id);

            return id;
        } catch (DataAccessException e) {
            logger.error("Unable to create tag (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean changeTag(User user, int id, String name, String description) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Tag.NAME_MAX_LENGTH;
        assert description != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE tags SET name = ?, description = ? WHERE user_id = ? AND id = ?",
                name, description, user.getId(), id
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to change a tag #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean deleteTag(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update("DELETE FROM tags WHERE user_id = ? AND id = ?", user.getId(), id);
            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows deleted using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to delete tag #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Tag> getTags(User user, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM tags t WHERE user_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                tagMapper, user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get tags (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Tag> getChainTags(User user, int chainId, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT t.* FROM tags t INNER JOIN tag_bindings b ON t.id = b.tag_id AND t.user_id = b.user_id " +
                "WHERE b.user_id = ? AND b.chain_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                tagMapper, user.getId(), chainId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get tags bound to chain #" + chainId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void setChainTags(User user, int chainId, Set<Integer> tags) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;
        assert tags != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            if (tags.isEmpty()) {
                template.update("DELETE FROM tag_bindings WHERE user_id = ? AND chain_id = ?", user.getId(), chainId);
            } else {
                final Object[] ids = tags.toArray();

                template.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO tag_bindings (user_id, tag_id, chain_id) " +
                            "SELECT ?, tag_id, ? FROM UNNEST(?) AS tag_id WHERE tag_id NOT IN (" +
                            "  SELECT tag_id FROM tag_bindings WHERE user_id = ? AND chain_id = ?" +
                            ")"
                    );
                    ps.setInt(1, user.getId());
                    ps.setInt(2, chainId);
                    ps.setArray(3, connection.createArrayOf("integer", ids));
                    ps.setInt(4, user.getId());
                    ps.setInt(5, chainId);
                    return ps;
                });

                template.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM tag_bindings WHERE user_id = ? AND chain_id = ? AND tag_id <> ALL(?)"
                    );
                    ps.setInt(1, user.getId());
                    ps.setInt(2, chainId);
                    ps.setArray(3, connection.createArrayOf("integer", ids));
                    return ps;
                });
            }
        } catch (DataAccessException e) {
            logger.error("Unable to set (bind/unbind) tags to chain #" + chainId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Tag getTag(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Tag> tags = template.query(
                "SELECT * FROM tags WHERE user_id = ? AND id = ?",
                tagMapper, user.getId(), id
            );

            if (CollectionUtils.isEmpty(tags)) {
                logger.debug("A tag #" + id + " (user #" + user.getId() + ") not exists");
            } else {
                if (tags.size() == 1) {
                    return tags.get(0);
                } else {
                    throw new DataIntegrityViolationException("A tag #" + id + " (user #" + user.getId() + ") duplicated " + tags.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a tag #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Override
    public Tag getTag(User user, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Tag.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Tag> tags = template.query(
                "SELECT * FROM tags WHERE user_id = ? AND name = ?",
                tagMapper, user.getId(), name
            );

            if (CollectionUtils.isEmpty(tags)) {
                logger.debug("A tag `" + name + "` (user #" + user.getId() + ") not exists");
            } else {
                if (tags.size() == 1) {
                    return tags.get(0);
                } else {
                    throw new DataIntegrityViolationException("A tag `" + name + "` (user #" + user.getId() + ") duplicated " + tags.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a tag `" + name + "` (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Required
    public void setTagFactory(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    protected class TagMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet row, int index) throws SQLException {
            Tag tag = tagFactory.create();
            tag.setId(row.getInt("id"));
            tag.setName(row.getString("name"));
            tag.setDescription(row.getString("description"));
            tag.setCreationTime(DaoUtils.asDate(row.getTimestamp("creation_time")));
            return tag;
        }
    }
}
