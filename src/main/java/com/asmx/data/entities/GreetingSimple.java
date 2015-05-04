package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 04.05.15 22:19.
 */
public class GreetingSimple implements Greeting {
    private int id;
    private String name;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
