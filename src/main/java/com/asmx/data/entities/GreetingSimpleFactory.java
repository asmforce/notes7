package com.asmx.data.entities;

import org.springframework.stereotype.Component;

/**
 * User: asmforce
 * Timestamp: 04.05.15 22:57.
 */
@Component
public class GreetingSimpleFactory implements GreetingFactory {
    @Override
    public Greeting create() {
        return new GreetingSimple();
    }
}
