package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.daos.errors.DataManagementException;
import com.asmx.data.entities.Keyword;
import com.asmx.data.entities.KeywordFactory;
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
 * Timestamp: 20.12.15 0:11.
**/
public class KeywordsDaoSimple extends Dao implements KeywordsDao {
    private static final Logger logger = Logger.getLogger(KeywordsDaoSimple.class);

    private static final Sorting DEFAULT_SORTING = Sorting.sorted("name", true);

    private KeywordFactory keywordFactory;
    private KeywordMapper keywordMapper = new KeywordMapper();

    @Override
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<String, String>() {{
            put("id", "k.id");
            put("name", "k.name");
            put("creation_time", "k.creation_time");
        }};
    }

    @Override
    public boolean checkKeywordExists(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM keywords WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check keyword #" + id + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkNameInUse(User user, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Keyword.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM keywords WHERE user_id = ? AND name = ?",
                Boolean.class,
                user.getId(), name
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check keyword `" + name + "` for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkKeywordInUse(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM keyword_bindings WHERE user_id = ? AND keyword_id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check if keyword #" + id + " is in use (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createKeyword(User user, Keyword keyword) {
        assert user != null;
        assert user.getId() > 0;
        assert keyword != null;
        assert StringUtils.isNotBlank(keyword.getName());
        assert StringUtils.length(keyword.getName()) <= Keyword.NAME_MAX_LENGTH;
        assert keyword.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO keywords (id, user_id, name, creation_time) VALUES (DEFAULT, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                ps.setString(2, keyword.getName());
                ps.setTimestamp(3, DaoUtils.asTimestamp(keyword.getCreationTime()));
                return ps;
            }, keyHolder);

            int id = keyHolder.getKey().intValue();
            keyword.setId(id);

            return id;
        } catch (DataAccessException e) {
            logger.error("Unable to create keyword (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void changeKeyword(User user, int id, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Keyword.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE keywords SET name = ? WHERE user_id = ? AND id = ?",
                name, user.getId(), id
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            }
            if (rows < 1) {
                throw new DataManagementException("The referenced keyword does not exist");
            }
        } catch (DataAccessException e) {
            logger.error("");
            throw e;
        }
    }

    @Override
    public boolean deleteKeyword(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update("DELETE FROM keywords WHERE user_id = ? AND id = ?", user.getId(), id);
            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows deleted using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to delete keyword #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Keyword> getKeywords(User user, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM keywords k WHERE user_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                keywordMapper, user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get keywords (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Keyword getKeyword(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Keyword> keywords = template.query(
                "SELECT * FROM keywords WHERE user_id = ? AND id = ?",
                keywordMapper, user.getId(), id
            );

            if (CollectionUtils.isEmpty(keywords)) {
                logger.debug("A keyword #" + id + " (user #" + user.getId() + ") not exists");
            } else {
                if (keywords.size() == 1) {
                    return keywords.get(0);
                } else {
                    throw new DataIntegrityViolationException("A keyword #" + id + " (user #" + user.getId() + ") duplicated " + keywords.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a keyword #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Required
    public void setKeywordFactory(KeywordFactory keywordFactory) {
        this.keywordFactory = keywordFactory;
    }

    protected class KeywordMapper implements RowMapper<Keyword> {
        @Override
        public Keyword mapRow(ResultSet row, int index) throws SQLException {
            Keyword keyword = keywordFactory.create();
            keyword.setId(row.getInt("id"));
            keyword.setName(row.getString("name"));
            keyword.setCreationTime(DaoUtils.asDate(row.getTimestamp("creation_time")));
            return keyword;
        }
    }
}
