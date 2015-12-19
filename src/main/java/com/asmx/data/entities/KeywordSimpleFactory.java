package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:41.
**/
public class KeywordSimpleFactory implements KeywordFactory {
    @Override
    public Keyword create() {
        return new KeywordSimple();
    }
}
