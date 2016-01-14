package com.asmx.data.daos;

import com.asmx.data.entities.User;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;

/**
 * User: asmforce
 * Timestamp: 07.01.16 14:58.
**/
public class ChainsDaoSimple extends Dao implements ChainsDao {
    private static final Logger logger = Logger.getLogger(ChainsDaoSimple.class);

    @Override
    public boolean checkChainExists(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM chains WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check a chain #" + id + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createChain(User user) {
        assert user != null;
        assert user.getId() > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO chains (id, user_id) VALUES (DEFAULT , ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                return ps;
            }, keyHolder);

            Number newId = keyHolder.getKey();
            return newId.intValue();
        } catch (DataAccessException e) {
            logger.error("Unable to create a chain (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean deleteChain(User user, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update("DELETE FROM chains WHERE user_id = ? AND id = ?", user.getId(), id);
            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows deleted using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to delete a chain #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkChainBindingExists(User user, int id, int spaceId) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert spaceId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM chain_bindings WHERE user_id = ? AND chain_id = ? AND space_id = ?",
                Boolean.class,
                user.getId(), id, spaceId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check a chain #" + id + " binding to space #" + spaceId + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean createChainBinding(User user, int id, int spaceId) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert spaceId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "INSERT INTO chain_bindings (user_id, space_id, chain_id) " +
                "SELECT ?, ?, ? WHERE NOT EXISTS (" +
                "  SELECT 1 FROM chain_bindings WHERE user_id = ? AND space_id = ? AND chain_id = ?" +
                ")",
                user.getId(), spaceId, id, user.getId(), spaceId, id
            );
            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows inserted using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to create a chain #" + id + " binding to space #" + spaceId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean deleteChainBinding(User user, int id, int spaceId) {
        assert user != null;
        assert user.getId() > 0;
        assert id > 0;
        assert spaceId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "DELETE FROM chain_bindings WHERE user_id = ? AND chain_id = ? AND space_id = ?",
                user.getId(), id, spaceId
            );
            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows deleted using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to delete a chain #" + id + " binding to space #" + spaceId + " (user #" + user.getId() + ")");
            throw e;
        }
    }
}
