package com.asmx.controllers.data.entities;

/**
 * User: asmforce
 * Timestamp: 02.08.15 20:12.
**/
public class SortingJson {
    private String criterion;
    private String direction;

    public SortingJson() {
    }

    public String getCriterion() {
        return criterion;
    }

    @SuppressWarnings("unused")
    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public String getDirection() {
        return direction;
    }

    @SuppressWarnings("unused")
    public void setDirection(String direction) {
        this.direction = direction;
    }
}
