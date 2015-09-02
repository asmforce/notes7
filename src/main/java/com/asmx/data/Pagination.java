package com.asmx.data;

/**
 * User: asmforce
 * Timestamp: 30.08.15 21:45.
**/
public class Pagination {
    private int begin;
    private int size;

    public static Pagination paginated(int begin, int size) {
        return new Pagination(begin > 0 ? begin : 0, size > 0 ? size : 0);
    }

    private Pagination(int begin, int size) {
        this.begin = begin;
        this.size = size;
    }

    public int getBegin() {
        return begin;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
