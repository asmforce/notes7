package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 01.08.15 15:24.
**/
public class NoteSimpleFactory implements NoteFactory {
    @Override
    public Note create() {
        return new NoteSimple();
    }
}
