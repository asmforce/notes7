package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:37.
**/
public class TagSimpleFactory implements TagFactory {
    @Override
    public Tag create() {
        return new TagSimple();
    }
}
