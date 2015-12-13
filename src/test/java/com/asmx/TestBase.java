package com.asmx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Random;

/**
 * User: asmforce
 * Timestamp: 03.12.15 3:38.
**/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test.xml")
@WebAppConfiguration
public abstract class TestBase {
    protected final Random random = new Random();

    @Before
    public void setUp() {
        // Do nothing
    }

    protected void assertThrows(String message, Runnable code, Class<? extends Throwable> cls) {
        Assert.assertNotNull(cls);

        boolean thrown = false;
        try {
            code.run();
        } catch (Throwable e) {
            if (cls.isInstance(e)) {
                thrown = true;
            } else {
                throw e;
            }
        }

        Assert.assertTrue("The assertion error is expected: " + message, thrown);
    }

    protected void assertThrows(String message, Runnable code) {
        assertThrows(message, code, AssertionError.class);
    }
}
