package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 04.05.15 22:57.
 */
public class GreetingSimpleFactory implements GreetingFactory {
    @Override
    public Greeting create() {
        return new GreetingSimple();
    }
}
