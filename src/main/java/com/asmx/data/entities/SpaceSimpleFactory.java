package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 18.06.15 1:00.
**/
public class SpaceSimpleFactory implements SpaceFactory {
    @Override
    public Space create() {
        return new SpaceSimple();
    }
}
