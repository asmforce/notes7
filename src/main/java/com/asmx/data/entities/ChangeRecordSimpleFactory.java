package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 29.11.15 17:31.
**/
public class ChangeRecordSimpleFactory implements ChangeRecordFactory {
    @Override
    public ChangeRecord create() {
        return new ChangeRecordSimple();
    }
}
