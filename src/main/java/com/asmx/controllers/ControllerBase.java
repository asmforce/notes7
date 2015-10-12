package com.asmx.controllers;

import com.asmx.controllers.data.entities.PaginationJson;
import com.asmx.controllers.data.entities.SortingJson;
import com.asmx.controllers.errors.ForgedRequestException;
import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import org.apache.commons.lang3.StringUtils;

/**
 * User: asmforce
 * Timestamp: 02.08.15 11:55.
**/
public abstract class ControllerBase {
    public static final String AJAX_HEADER = "X-Requested-With=XMLHttpRequest";
    public static final String CONTENT_TYPE_JSON = "application/json";

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

    public static Sorting sorting(String criterion, String direction, boolean defaultDirection) {
        return Sorting.sorted(criterion, sortingDirection(direction, defaultDirection));
    }

    public static Sorting sorting(String criterion, String direction) {
        return Sorting.sorted(criterion, sortingDirection(direction, false));
    }

    public static Sorting sorting(String criterion, boolean ascending) {
        return Sorting.sorted(criterion, ascending);
    }

    public static Pagination pagination(int begin, int size) {
        return Pagination.paginated(begin, size);
    }

    public static Pagination pagination(PaginationJson paginationJson) throws ForgedRequestException {
        if (PaginationJson.isValid(paginationJson)) {
            return Pagination.paginated(paginationJson.getBegin(), paginationJson.getSize());
        } else {
            throw new ForgedRequestException("Invalid pagination parameters");
        }
    }
}
