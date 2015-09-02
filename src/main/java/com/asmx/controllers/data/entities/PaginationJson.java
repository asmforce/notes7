package com.asmx.controllers.data.entities;

/**
 * User: asmforce
 * Timestamp: 30.08.15 22:55.
**/
public class PaginationJson {
    private Integer begin;
    private Integer size;

    public static boolean isValid(PaginationJson paginationJson) {
        if (paginationJson == null) {
            return false;
        }
        return paginationJson.isValid();
    }

    public PaginationJson() {
    }

    public Integer getBegin() {
        return begin;
    }

    @SuppressWarnings("unused")
    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getSize() {
        return size;
    }

    @SuppressWarnings("unused")
    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isValid() {
        if (begin == null || size == null) {
            return false;
        } else {
            return begin >= 0 && size > 0;
        }
    }
}
