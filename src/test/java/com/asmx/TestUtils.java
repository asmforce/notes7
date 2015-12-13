package com.asmx;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 * User: asmforce
 * Timestamp: 05.12.15 1:05.
**/
public class TestUtils {
    protected final Random random = new Random();

    @SuppressWarnings("unchecked")
    public <T> T any(T... items) {
        Assert.assertNotNull(items);
        Assert.assertTrue(items.length > 0);

        return items[random.nextInt(items.length)];
    }

    public <T> T any(Collection<T> items) {
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());

        final int index = random.nextInt(items.size());

        T value = null;
        Iterator<T> iterator = items.iterator();
        for (int i = 0; i <= index; i++) {
            value = iterator.next();
        }

        return value;
    }

    public <T> void generate(Collection<T> collection, int count, Supplier<T> supplier) {
        Assert.assertTrue(count >= 0);

        int attempts = 0;
        while (collection.size() < count) {
            if (collection.add(supplier.get())) {
                attempts = 0;
            } else {
                attempts++;
                Assert.assertTrue("A collection has rejected too many supplied items", attempts < 10);
            }
        }
    }

    public <T> T generateUniqueOneMore(Set<T> collection, Supplier<T> supplier) {
        final int count = collection.size() + 1;

        T value = null;
        int attempts = 0;

        while (collection.size() < count) {
            value = supplier.get();
            if (collection.add(value)) {
                attempts = 0;
            } else {
                attempts++;
                Assert.assertTrue("A collection has rejected too many supplied items", attempts < 10);
            }
        }

        return value;
    }

    public <T> Set<T> generateUnique(int count, Supplier<T> supplier) {
        Set<T> collection = new HashSet<>();
        generate(collection, count, supplier);
        return collection;
    }

    public String generateString(int size, boolean alphanumeric) {
        if (size > 0) {
            if (alphanumeric) {
                return RandomStringUtils.random(size, true, true);
            } else {
                StringBuilder sb = new StringBuilder();
                while (sb.length() < size) {
                    int code = random.nextInt(Character.MAX_VALUE);
                    if (code > 0 && Character.isDefined(code) && !Character.isSurrogate((char) code)) {
                        sb.append((char) code);
                    }
                }
                return sb.toString();
            }
        } else {
            return "";
        }
    }

    public String generateString(int size) {
        return generateString(size, false);
    }
}
