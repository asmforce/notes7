package com.asmx.data;

import org.apache.commons.lang3.StringUtils;

/**
 * User: asmforce
 * Timestamp: 02.08.15 0:25.
**/
public class Sorting {
    private String criterion;
    private boolean ascending;

    public static Sorting sorted(String criterion, boolean ascending) {
        if (StringUtils.isEmpty(criterion)) {
            return null;
        } else {
            return new Sorting(criterion, ascending);
        }
    }

    public static Sorting ascending(String criterion) {
        return sorted(criterion, true);
    }

    public static Sorting descending(String criterion) {
        return sorted(criterion, false);
    }

    private Sorting(String criterion, boolean ascending) {
        this.criterion = criterion.toLowerCase();
        this.ascending = ascending;
    }

    public String criterion() {
        return criterion;
    }

    public boolean ascending() {
        return ascending;
    }
}
