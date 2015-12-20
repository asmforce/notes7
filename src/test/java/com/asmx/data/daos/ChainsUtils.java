package com.asmx.data.daos;

import com.asmx.TestUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * User: asmforce
 * Timestamp: 20.12.15 1:38.
**/
public class ChainsUtils extends TestUtils {
    private JdbcTemplate template;

    public void insert(int chainId, int userId) {
        final String statement = "INSERT INTO chains (id, user_id) VALUES (?, ?)";
        template.update(statement, chainId, userId);
    }

    public boolean delete(int chainId) {
        final String statement = "DELETE FROM chains WHERE id = ?";
        return template.update(statement, chainId) > 0;
    }

    public boolean exists(int chainId, int userId) {
        final String statement = "SELECT COUNT(*) > 0 FROM chains WHERE user_id = ? AND id = ?";
        return template.queryForObject(statement, Boolean.class, userId, chainId);
    }

    public void insertBinding(int chainId, int spaceId, int userId) {
        final String statement = "INSERT INTO chain_bindings (chain_id, space_id, user_id) VALUES (?, ?, ?)";
        template.update(statement, chainId, spaceId, userId);
    }

    public boolean deleteBinding(int chainId, int spaceId) {
        final String statement = "DELETE FROM chain_bindings WHERE chain_id = ? AND space_id = ?";
        return template.update(statement, chainId, spaceId) > 0;
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
