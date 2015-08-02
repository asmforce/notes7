package com.asmx.controllers;

import com.asmx.controllers.data.entities.SortingJson;
import com.asmx.data.Sorting;
import org.apache.commons.lang3.StringUtils;

/**
 * User: asmforce
 * Timestamp: 02.08.15 11:55.
**/
public abstract class ControllerUtils {
    public static boolean sortingDirection(String direction, boolean defaultDirection) {
        if (StringUtils.isNotBlank(direction)) {
            if ("ascending".startsWith(direction)) {
                return true;
            }
            if ("descending".startsWith(direction)) {
                return false;
            }
        }
        return defaultDirection;
    }

    public static boolean sortingDirection(String direction) {
        // Use ascending order when direction is omitted or invalid value passed
        return sortingDirection(direction, true);
    }

    public static Sorting sorting(SortingJson sortingJson) {
        if (sortingJson == null) {
            return null;
        }
        return sorting(sortingJson.getCriterion(), sortingDirection(sortingJson.getDirection()));
    }

    public static Sorting sorting(String criteria, String direction, boolean defaultDirection) {
        return Sorting.sorted(criteria, sortingDirection(direction, defaultDirection));
    }

    public static Sorting sorting(String criteria, String direction) {
        return Sorting.sorted(criteria, sortingDirection(direction, false));
    }

    public static Sorting sorting(String criteria, boolean ascending) {
        return Sorting.sorted(criteria, ascending);
    }
}
