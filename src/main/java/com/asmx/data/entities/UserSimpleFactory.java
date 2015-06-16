package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 06.06.15 18:08.
**/
public class UserSimpleFactory implements UserFactory {
    public User create() {
        return new UserSimple();
    }
}
