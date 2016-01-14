package com.asmx.data.daos;

import com.asmx.data.Sorting;
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
import java.util.Set;

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
    public boolean changeKeyword(User user, int id, String name) {
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
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to update a keyword #" + id + " (user #" + user.getId() + ")");
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
    public List<Keyword> getChainKeywords(User user, int chainId, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT k.* FROM keywords k INNER JOIN keyword_bindings b ON k.id = b.keyword_id AND k.user_id = b.user_id " +
                "WHERE b.user_id = ? AND b.chain_id = ? " + getSortingClause(sorting, DEFAULT_SORTING),
                keywordMapper, user.getId(), chainId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get keywords bound to chain #" + chainId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void setChainKeywords(User user, int chainId, Set<Integer> keywords) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;
        assert keywords != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            if (keywords.isEmpty()) {
                template.update("DELETE FROM keyword_bindings WHERE user_id = ? AND chain_id = ?", user.getId(), chainId);
            } else {
                final Object[] ids = keywords.toArray();

                template.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO keyword_bindings (user_id, keyword_id, chain_id) " +
                            "SELECT ?, keyword_id, ? FROM UNNEST(?) AS keyword_id WHERE keyword_id NOT IN (" +
                            "  SELECT keyword_id FROM keyword_bindings WHERE user_id = ? AND chain_id = ?" +
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
                        "DELETE FROM keyword_bindings WHERE user_id = ? AND chain_id = ? AND keyword_id <> ALL(?)"
                    );
                    ps.setInt(1, user.getId());
                    ps.setInt(2, chainId);
                    ps.setArray(3, connection.createArrayOf("integer", ids));
                    return ps;
                });
            }
        } catch (DataAccessException e) {
            logger.error("Unable to set (bind/unbind) keywords to chain #" + chainId + " (user #" + user.getId() + ")");
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

    @Override
    public Keyword getKeyword(User user, String name) {
        assert user != null;
        assert user.getId() > 0;
        assert StringUtils.isNotBlank(name);
        assert StringUtils.length(name) <= Keyword.NAME_MAX_LENGTH;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Keyword> keywords = template.query(
                "SELECT * FROM keywords WHERE user_id = ? AND name = ?",
                keywordMapper, user.getId(), name
            );

            if (CollectionUtils.isEmpty(keywords)) {
                logger.debug("A keyword `" + name + "` (user #" + user.getId() + ") not exists");
            } else {
                if (keywords.size() == 1) {
                    return keywords.get(0);
                } else {
                    throw new DataIntegrityViolationException("A keyword `" + name + "` (user #" + user.getId() + ") duplicated " + keywords.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a keyword `" + name + "` (user #" + user.getId() + ")");
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
