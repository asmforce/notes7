package com.asmx.data.daos;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 05.05.15 23:08.
**/
public class Dao {
    private static final Logger logger = Logger.getLogger(Dao.class);

    private JdbcTemplate jdbcTemplate;

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected String getPaginationClause(Pagination pagination) {
        return "OFFSET " + pagination.getBegin() + " LIMIT " + pagination.getSize();
    }

    /**
     * Maps allowed sorting criterion to table columns. Works as injection prevention mechanism.
     * To be overridden in the subclasses.
    **/
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<>();
    }

    protected String getSortingClause(Sorting ...sortings) {
        StringBuilder sqlClauseBuilder = new StringBuilder("");

        if (sortings != null && sortings.length > 0) {
            Map<String, String> expectedCriteria = getExpectedSortingCriteriaMap();
            Set<String> usedCriteria = new HashSet<>();
            boolean hasPrevious = false;

            for (Sorting s : sortings) {
                if (s == null) {
                    continue;
                }

                String criterion = s.criterion();
                String column = expectedCriteria.get(criterion);

                if (StringUtils.isNotEmpty(column)) {
                    if (usedCriteria.contains(column)) {
                        logger.debug("Duplicated sorting column `" + column + "`");
                    } else {
                        usedCriteria.add(column);

                        if (hasPrevious) {
                            sqlClauseBuilder.append(", ");
                        } else {
                            sqlClauseBuilder.append("ORDER BY ");
                            hasPrevious = true;
                        }

                        sqlClauseBuilder.append(column)
                                .append(s.ascending() ? " ASC" : " DESC");
                    }
                } else {
                    logger.debug("Requested sorting for unknown criterion `" + criterion + "`");
                }
            }
        }

        return sqlClauseBuilder.toString();
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
