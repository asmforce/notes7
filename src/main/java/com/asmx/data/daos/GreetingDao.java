package com.asmx.data.daos;

import com.asmx.data.entities.Greeting;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 04.05.15 22:58.
 */
public interface GreetingDao {
    List<Greeting> getGreetings();
    Greeting getGreeting(int id);
    boolean putGreeting(Greeting greeting);
}
