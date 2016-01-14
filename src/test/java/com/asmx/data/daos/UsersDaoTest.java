package com.asmx.data.daos;

import com.asmx.data.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 02.12.15 2:51.
**/
public class UsersDaoTest extends DaoTestBase {
    @Resource
    private UsersDao usersDao;
    @Resource
    private UsersUtils usersUtils;

    @Override
    public void setUp() {
        super.setUp();
        usersUtils.clearGeneratedNames();
    }

    /**
     * See {@link UsersDao#checkUserExists(int)}.
    **/
    @Test
    public void testInvalidArgumentsCheckUserExists() {
        final int invalidIds[] = {0, -1, -2, -3, -10, -11, -100, -123, -1000 -1234, -9999};

        for (int invalidId : invalidIds) {
            assertThrows("id <= 0", () -> usersDao.checkUserExists(invalidId));
        }
    }

    /**
     * See {@link UsersDao#checkUserExists(int)}.
    **/
    @Test
    public void testCheckUserExists() {
        final int idsToUse[] = {1, 2, 80, 90, 123, 234, 345, 456, 567, 678, 789};
        final String[] namesToUse = usersUtils.generateUnique(idsToUse.length, usersUtils::generateName)
            .toArray(new String[idsToUse.length]);

        Set<Integer> usedIds = new HashSet<>();
        Set<Integer> unusedIds = new HashSet<Integer>() {{
            for (int id : idsToUse) {
                add(id);
            }
        }};

        for (int id : idsToUse) {
            Assert.assertFalse(usersDao.checkUserExists(id));
        }

        for (int i = 0; i < idsToUse.length; i++) {
            final int newId = idsToUse[i];
            final String newName = namesToUse[i];

            assertQuery("SELECT COUNT(*) = ? FROM users", i);
            assertQuery("SELECT COUNT(*) = 0 FROM users WHERE id = ?", newId);

            User user = usersUtils.generateInstanceWithUniqueName();
            user.setId(newId);
            user.setName(newName);
            usersUtils.insert(user);

            unusedIds.remove(newId);
            usedIds.add(newId);

            assertQuery("SELECT COUNT(*) = ? FROM users", i + 1);
            assertQuery("SELECT COUNT(*) = 1 FROM users WHERE id = ?", newId);

            for (int usedId : usedIds) {
                Assert.assertTrue(usersDao.checkUserExists(usedId));
            }
            for (int unusedId : unusedIds) {
                Assert.assertFalse(usersDao.checkUserExists(unusedId));
            }
        }
    }

    /**
     * See {@link UsersDao#checkNameInUse(String)}.
    **/
    @Test
    public void testInvalidArgumentsCheckNameInUse() {
        for (int i = 0; i <= User.NAME_MAX_LENGTH; i++) {
            final int times = i;
            assertThrows("blank name", () -> usersDao.checkNameInUse(StringUtils.repeat(" ", times)));
        }

        for (int i = 0; i <= User.NAME_MAX_LENGTH; i++) {
            final int times = i;
            assertThrows("blank name", () -> usersDao.checkNameInUse(StringUtils.repeat("\n", times)));
        }

        assertThrows("name is too long", () -> usersDao.checkNameInUse(StringUtils.repeat("a", User.NAME_MAX_LENGTH + 1)));
        assertThrows("name is too long", () -> usersDao.checkNameInUse(StringUtils.repeat("?", User.NAME_MAX_LENGTH + 10)));
    }

    /**
     * See {@link UsersDao#checkNameInUse(String)}.
    **/
    @Test
    public void testCheckNameInUse() {
        final int count = 30;

        final Integer idsToUse[] = usersUtils.generateUnique(count, () -> random.nextInt(100000) + 1)
            .toArray(new Integer[count]);

        final String[] namesToUse = usersUtils.generateUnique(count, usersUtils::generateName)
            .toArray(new String[count]);

        assertQuery("SELECT COUNT(*) = 0 FROM users");

        for (int i = 0; i < count; i++) {
            Assert.assertFalse(usersDao.checkNameInUse(namesToUse[i]));
        }

        for (int i = 0; i < count; i++) {
            final int newId = idsToUse[i];
            final String newName = namesToUse[i];

            assertQuery("SELECT COUNT(*) = ? FROM users", i);
            assertQuery("SELECT COUNT(*) = 0 FROM users WHERE name = ?", newName);

            User user = usersUtils.generateInstanceWithUniqueName();
            user.setId(newId);
            user.setName(newName);
            usersUtils.insert(user);

            assertQuery("SELECT COUNT(*) = ? FROM users", i + 1);
            assertQuery("SELECT COUNT(*) = 1 FROM users WHERE name = ?", newName);

            for (int k = 0; k <= i; k++) {
                Assert.assertTrue(usersDao.checkNameInUse(namesToUse[k]));
            }

            for (int k = i + 1; k < count; k++) {
                Assert.assertFalse(usersDao.checkNameInUse(namesToUse[k]));
            }
        }
    }

    /**
     * See {@link UsersDao#createUser(User)}.
    **/
    @Test
    public void testInvalidArgumentsCreateUser() {
        assertQuery("SELECT COUNT(*) = 0 FROM users");

        assertThrows("user is null", () -> usersDao.createUser(null));

        User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setName(null);
        assertThrows("name is null", () -> usersDao.createUser(user1));
        user1.setName("");
        assertThrows("name is empty", () -> usersDao.createUser(user1));
        user1.setName(" ");
        assertThrows("name is blank", () -> usersDao.createUser(user1));
        user1.setName("   \n");
        assertThrows("name is blank", () -> usersDao.createUser(user1));
        user1.setName(StringUtils.repeat("a", User.NAME_MAX_LENGTH + 1));
        assertThrows("name is too long", () -> usersDao.createUser(user1));
        user1.setName(StringUtils.repeat("b", User.NAME_MAX_LENGTH + 10));
        assertThrows("name is too long", () -> usersDao.createUser(user1));

        User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setKey(null);
        assertThrows("key is null", () -> usersDao.createUser(user2));
        user2.setKey("");
        assertThrows("key is empty", () -> usersDao.createUser(user2));
        user2.setKey(StringUtils.repeat("c", User.KEY_MAX_LENGTH + 1));
        assertThrows("key is too long", () -> usersDao.createUser(user2));
        user2.setKey(StringUtils.repeat("d", User.KEY_MAX_LENGTH + 10));
        assertThrows("key is too long", () -> usersDao.createUser(user2));

        User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setLanguage(null);
        assertThrows("language is null", () -> usersDao.createUser(user3));
        user3.setLanguage("");
        assertThrows("language is empty", () -> usersDao.createUser(user3));
        user3.setLanguage(StringUtils.repeat("e", User.LANGUAGE_MAX_LENGTH + 1));
        assertThrows("language is too long", () -> usersDao.createUser(user3));
        user3.setLanguage(StringUtils.repeat("f", User.LANGUAGE_MAX_LENGTH + 10));
        assertThrows("language is too long", () -> usersDao.createUser(user3));

        User user4 = usersUtils.generateInstanceWithUniqueName();
        user4.setTimezone(null);
        assertThrows("timezone is null", () -> usersDao.createUser(user4));
        user4.setTimezone("");
        assertThrows("timezone is empty", () -> usersDao.createUser(user4));
        user4.setTimezone(StringUtils.repeat("g", User.TIMEZONE_MAX_LENGTH + 1));
        assertThrows("timezone is too long", () -> usersDao.createUser(user4));
        user4.setTimezone(StringUtils.repeat("h", User.TIMEZONE_MAX_LENGTH + 10));
        assertThrows("timezone is too long", () -> usersDao.createUser(user4));

        assertQuery("SELECT COUNT(*) = 0 FROM users");
    }

    /**
     * See {@link UsersDao#createUser(User)}.
    **/
    @Test
    public void testConstraintsCreateUser() {
        final int count1 = 5;
        final int count2 = 10;

        assertQuery("SELECT COUNT(*) = 0 FROM users");

        for (int i = 0; i < count1; i++) {
            usersDao.createUser(usersUtils.generateInstanceWithUniqueName());
        }
        assertQuery("SELECT COUNT(*) = ? FROM users", count1);

        for (String name : usersUtils.getGeneratedNames()) {
            User user = usersUtils.generateInstance();
            user.setName(name);
            assertThrows("name duplication", () -> usersDao.createUser(user), DataAccessException.class);
        }
        assertQuery("SELECT COUNT(*) = ? FROM users", count1);

        for (int i = 0; i < count2; i++) {
            usersDao.createUser(usersUtils.generateInstanceWithUniqueName());
        }
        assertQuery("SELECT COUNT(*) = ? FROM users", count1 + count2);

        for (String name : usersUtils.getGeneratedNames()) {
            User user = usersUtils.generateInstance();
            user.setName(name);
            assertThrows("name duplication", () -> usersDao.createUser(user), DataAccessException.class);
        }
        assertQuery("SELECT COUNT(*) = ? FROM users", count1 + count2);
    }

    /**
     * See {@link UsersDao#createUser(User)}.
    **/
    @Test
    public void testCreateUser() {
        final int count = 30;

        for (int i = 0; i < count; i++) {
            assertQuery("SELECT COUNT(*) = ? FROM users", i);

            User user = usersUtils.generateInstanceWithUniqueName();

            User expected = usersUtils.instantiate();
            expected.setName(user.getName());
            expected.setKey(user.getKey());
            expected.setTimezone(user.getTimezone());
            expected.setLanguage(user.getLanguage());

            int newId = usersDao.createUser(user);

            Assert.assertTrue(newId > 0);
            Assert.assertTrue(newId == user.getId());

            expected.setId(newId);
            assertEquals(expected, user);

            assertQuery("SELECT COUNT(*) = ? FROM users", i + 1);
            assertQuery("SELECT COUNT(*) = 1 FROM users WHERE id = ?", newId);

            assertEquals(expected, usersUtils.select(newId));
        }
    }

    /**
     * See {@link UsersDao#changeUser(User)}.
    **/
    @Test
    public void testInvalidArgumentsChangeUser() {
        assertQuery("SELECT COUNT(*) = 0 FROM users");

        final int USER_ID = 234;
        User origin = usersUtils.instantiate();
        origin.setId(USER_ID);
        origin.setName("admin");
        origin.setKey("a1b2c3d4e5f6g7h8i9");
        origin.setLanguage("uk");
        origin.setTimezone("Europe/Kiev");

        usersUtils.insert(origin);

        assertQuery("SELECT COUNT(*) = 1 FROM users");
        assertEquals(origin, usersUtils.select(USER_ID));

        assertThrows("user is null", () -> usersDao.changeUser(null));

        User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(0);
        assertThrows("id <= 0", () -> usersDao.changeUser(user1));
        user1.setId(-1);
        assertThrows("id <= 0", () -> usersDao.changeUser(user1));
        user1.setId(-10);
        assertThrows("id <= 0", () -> usersDao.changeUser(user1));

        User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setName(null);
        assertThrows("name is null", () -> usersDao.changeUser(user2));
        user2.setName("");
        assertThrows("name is empty", () -> usersDao.changeUser(user2));
        user2.setName(" ");
        assertThrows("name is blank", () -> usersDao.changeUser(user2));
        user2.setName("   \n");
        assertThrows("name is blank", () -> usersDao.changeUser(user2));
        user2.setName(StringUtils.repeat("a", User.NAME_MAX_LENGTH + 1));
        assertThrows("name is too long", () -> usersDao.changeUser(user2));
        user2.setName(StringUtils.repeat("b", User.NAME_MAX_LENGTH + 10));
        assertThrows("name is too long", () -> usersDao.changeUser(user2));

        User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setKey(null);
        assertThrows("key is null", () -> usersDao.changeUser(user3));
        user3.setKey("");
        assertThrows("key is empty", () -> usersDao.changeUser(user3));
        user3.setKey(StringUtils.repeat("c", User.KEY_MAX_LENGTH + 1));
        assertThrows("key is too long", () -> usersDao.changeUser(user3));
        user3.setKey(StringUtils.repeat("d", User.KEY_MAX_LENGTH + 10));
        assertThrows("key is too long", () -> usersDao.changeUser(user3));

        User user4 = usersUtils.generateInstanceWithUniqueName();
        user4.setLanguage(null);
        assertThrows("language is null", () -> usersDao.changeUser(user4));
        user4.setLanguage("");
        assertThrows("language is empty", () -> usersDao.changeUser(user4));
        user4.setLanguage(StringUtils.repeat("e", User.LANGUAGE_MAX_LENGTH + 1));
        assertThrows("language is too long", () -> usersDao.changeUser(user4));
        user4.setLanguage(StringUtils.repeat("f", User.LANGUAGE_MAX_LENGTH + 10));
        assertThrows("language is too long", () -> usersDao.changeUser(user4));

        User user5 = usersUtils.generateInstanceWithUniqueName();
        user5.setTimezone(null);
        assertThrows("timezone is null", () -> usersDao.changeUser(user5));
        user5.setTimezone("");
        assertThrows("timezone is empty", () -> usersDao.changeUser(user5));
        user5.setTimezone(StringUtils.repeat("g", User.TIMEZONE_MAX_LENGTH + 1));
        assertThrows("timezone is too long", () -> usersDao.changeUser(user5));
        user5.setTimezone(StringUtils.repeat("h", User.TIMEZONE_MAX_LENGTH + 10));
        assertThrows("timezone is too long", () -> usersDao.changeUser(user5));

        assertQuery("SELECT COUNT(*) = 1 FROM users");
        assertEquals(origin, usersUtils.select(USER_ID));
    }

    /**
     * See {@link UsersDao#changeUser(User)}.
    **/
    @Test
    public void testConstraintsChangeUser() {
        final int USER_ID1 = 123;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 234;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 345;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        usersUtils.insert(user3);

        final int count1 = 15;
        final int count2 = 30;

        for (int i = 0; i < count1; i++) {
            switch (i % 3) {
            case 0:
                user1.setName(usersUtils.generateUniqueName());
                Assert.assertTrue(usersDao.changeUser(user1));
                break;
            case 1:
                user2.setName(usersUtils.generateUniqueName());
                Assert.assertTrue(usersDao.changeUser(user2));
                break;
            case 2:
                user3.setName(usersUtils.generateUniqueName());
                Assert.assertTrue(usersDao.changeUser(user3));
                break;
            }

            assertEquals(user1, usersUtils.select(USER_ID1));
            assertEquals(user2, usersUtils.select(USER_ID2));
            assertEquals(user3, usersUtils.select(USER_ID3));
        }

        for (int i = 0; i < count2; i++) {
            String oldName;

            switch (i % 6) {
            case 0:
                oldName = user1.getName();
                user1.setName(user2.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user1), DataAccessException.class);
                user1.setName(oldName);
                break;
            case 1:
                oldName = user1.getName();
                user1.setName(user3.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user1), DataAccessException.class);
                user1.setName(oldName);
                break;
            case 2:
                oldName = user2.getName();
                user2.setName(user1.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user2), DataAccessException.class);
                user2.setName(oldName);
                break;
            case 3:
                oldName = user2.getName();
                user2.setName(user3.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user2), DataAccessException.class);
                user2.setName(oldName);
                break;
            case 4:
                oldName = user3.getName();
                user3.setName(user1.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user3), DataAccessException.class);
                user3.setName(oldName);
                break;
            case 5:
                oldName = user3.getName();
                user3.setName(user2.getName());
                assertThrows("name duplication", () -> usersDao.changeUser(user3), DataAccessException.class);
                user3.setName(oldName);
                break;
            }

            assertEquals(user1, usersUtils.select(USER_ID1));
            assertEquals(user2, usersUtils.select(USER_ID2));
            assertEquals(user3, usersUtils.select(USER_ID3));
        }
    }

    /**
     * See {@link UsersDao#changeUser(User)}.
    **/
    @Test
    public void testChangeUser() {
        assertQuery("SELECT COUNT(*) = 0 FROM users");

        final int USER_ID1 = 123;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        Assert.assertFalse("id references nothing", usersDao.changeUser(user1));
        usersUtils.insert(user1);

        final int USER_ID2 = 234;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        Assert.assertFalse("id references nothing", usersDao.changeUser(user2));
        usersUtils.insert(user2);

        final int USER_ID3 = 345;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        Assert.assertFalse("id references nothing", usersDao.changeUser(user3));
        usersUtils.insert(user3);

        assertQuery("SELECT COUNT(*) = 3 FROM users");

        assertEquals(user1, usersUtils.select(USER_ID1));
        assertEquals(user2, usersUtils.select(USER_ID2));
        assertEquals(user3, usersUtils.select(USER_ID3));

        usersUtils.generateInstanceWithUniqueName(user1);
        user1.setId(USER_ID1);
        Assert.assertTrue(usersDao.changeUser(user1));

        assertEquals(user1, usersUtils.select(USER_ID1));
        assertEquals(user2, usersUtils.select(USER_ID2));
        assertEquals(user3, usersUtils.select(USER_ID3));

        usersUtils.generateInstanceWithUniqueName(user2);
        user2.setId(USER_ID2);
        Assert.assertTrue(usersDao.changeUser(user2));

        assertEquals(user1, usersUtils.select(USER_ID1));
        assertEquals(user2, usersUtils.select(USER_ID2));
        assertEquals(user3, usersUtils.select(USER_ID3));

        usersUtils.generateInstanceWithUniqueName(user3);
        user3.setId(USER_ID3);
        Assert.assertTrue(usersDao.changeUser(user3));

        assertEquals(user1, usersUtils.select(USER_ID1));
        assertEquals(user2, usersUtils.select(USER_ID2));
        assertEquals(user3, usersUtils.select(USER_ID3));

        usersUtils.generateInstanceWithUniqueName(user1);
        user1.setId(USER_ID1);
        Assert.assertTrue(usersDao.changeUser(user1));

        assertEquals(user1, usersUtils.select(USER_ID1));
        assertEquals(user2, usersUtils.select(USER_ID2));
        assertEquals(user3, usersUtils.select(USER_ID3));

        assertQuery("SELECT COUNT(*) = 3 FROM users");
    }

    /**
     * See {@link UsersDao#getUser(int)}.
    **/
    @Test
    public void testInvalidArgumentsGetUser() {
        assertThrows("id <= 0", () -> usersDao.getUser(0));
        assertThrows("id <= 0", () -> usersDao.getUser(-1));
        assertThrows("id <= 0", () -> usersDao.getUser(-100));
    }

    /**
     * See {@link UsersDao#getUser(int)}.
    **/
    @Test
    public void testGetUser() {
        final int USER_ID1 = 123;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        user1.setName(usersUtils.generateUniqueName());

        final int USER_ID2 = 234;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        user2.setName(usersUtils.generateUniqueName());

        final int USER_ID3 = 345;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        user3.setName(usersUtils.generateUniqueName());

        final int USER_ID4 = 456;
        final User user4 = usersUtils.generateInstanceWithUniqueName();
        user4.setId(USER_ID4);
        user4.setName(usersUtils.generateUniqueName());

        Assert.assertNull(usersDao.getUser(USER_ID1));
        Assert.assertNull(usersDao.getUser(USER_ID2));
        Assert.assertNull(usersDao.getUser(USER_ID3));
        Assert.assertNull(usersDao.getUser(USER_ID4));

        usersUtils.insert(user1);
        usersUtils.insert(user3);

        assertEquals(user1, usersDao.getUser(USER_ID1));
        Assert.assertNull(usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        Assert.assertNull(usersDao.getUser(USER_ID4));

        usersUtils.insert(user4);

        assertEquals(user1, usersDao.getUser(USER_ID1));
        Assert.assertNull(usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        assertEquals(user4, usersDao.getUser(USER_ID4));

        usersUtils.insert(user2);

        assertEquals(user1, usersDao.getUser(USER_ID1));
        assertEquals(user2, usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        assertEquals(user4, usersDao.getUser(USER_ID4));

        usersUtils.generateInstanceWithUniqueName(user1);
        user1.setId(USER_ID1);
        usersUtils.update(user1);

        usersUtils.generateInstanceWithUniqueName(user2);
        user2.setId(USER_ID2);
        usersUtils.update(user2);

        assertEquals(user1, usersDao.getUser(USER_ID1));
        assertEquals(user2, usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        assertEquals(user4, usersDao.getUser(USER_ID4));

        usersUtils.generateInstanceWithUniqueName(user3);
        user3.setId(USER_ID3);
        usersUtils.update(user3);

        usersUtils.generateInstanceWithUniqueName(user4);
        user4.setId(USER_ID4);
        usersUtils.update(user4);

        assertEquals(user1, usersDao.getUser(USER_ID1));
        assertEquals(user2, usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        assertEquals(user4, usersDao.getUser(USER_ID4));

        usersUtils.delete(USER_ID1);
        usersUtils.delete(USER_ID4);

        Assert.assertNull(usersDao.getUser(USER_ID1));
        assertEquals(user2, usersDao.getUser(USER_ID2));
        assertEquals(user3, usersDao.getUser(USER_ID3));
        Assert.assertNull(usersDao.getUser(USER_ID4));

        usersUtils.delete(USER_ID2);
        usersUtils.delete(USER_ID3);

        Assert.assertNull(usersDao.getUser(USER_ID1));
        Assert.assertNull(usersDao.getUser(USER_ID2));
        Assert.assertNull(usersDao.getUser(USER_ID3));
        Assert.assertNull(usersDao.getUser(USER_ID4));
    }

    /**
     * See {@link UsersDao#getUser(String)}.
    **/
    @Test
    public void testInvalidArgumentsGetUserByName() {
        assertThrows("name is null", () -> usersDao.getUser(null));
        assertThrows("name is empty", () -> usersDao.getUser(""));
    }

    /**
     * See {@link UsersDao#getUser(String)}.
    **/
    @Test
    public void testGetUserByName() {
        final int USER_ID1 = 123;
        final String USER_NAME1 = "user123";
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        user1.setName(USER_NAME1);

        final int USER_ID2 = 234;
        final String USER_NAME2 = "user234";
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        user2.setName(USER_NAME2);

        final int USER_ID3 = 345;
        final String USER_NAME3 = "user345";
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        user3.setName(USER_NAME3);

        final int USER_ID4 = 456;
        final String USER_NAME4 = "user456";
        final User user4 = usersUtils.generateInstanceWithUniqueName();
        user4.setId(USER_ID4);
        user4.setName(USER_NAME4);

        Assert.assertNull(usersDao.getUser(USER_NAME1));
        Assert.assertNull(usersDao.getUser(USER_NAME2));
        Assert.assertNull(usersDao.getUser(USER_NAME3));
        Assert.assertNull(usersDao.getUser(USER_NAME4));

        usersUtils.insert(user1);
        usersUtils.insert(user3);

        assertEquals(user1, usersDao.getUser(USER_NAME1));
        Assert.assertNull(usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        Assert.assertNull(usersDao.getUser(USER_NAME4));

        usersUtils.insert(user4);

        assertEquals(user1, usersDao.getUser(USER_NAME1));
        Assert.assertNull(usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        assertEquals(user4, usersDao.getUser(USER_NAME4));

        usersUtils.insert(user2);

        assertEquals(user1, usersDao.getUser(USER_NAME1));
        assertEquals(user2, usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        assertEquals(user4, usersDao.getUser(USER_NAME4));

        usersUtils.generateInstanceWithUniqueName(user1);
        user1.setId(USER_ID1);
        user1.setName(USER_NAME1);
        usersUtils.update(user1);

        usersUtils.generateInstanceWithUniqueName(user2);
        user2.setId(USER_ID2);
        user2.setName(USER_NAME2);
        usersUtils.update(user2);

        assertEquals(user1, usersDao.getUser(USER_NAME1));
        assertEquals(user2, usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        assertEquals(user4, usersDao.getUser(USER_NAME4));

        usersUtils.generateInstanceWithUniqueName(user3);
        user3.setId(USER_ID3);
        user3.setName(USER_NAME3);
        usersUtils.update(user3);

        usersUtils.generateInstanceWithUniqueName(user4);
        user4.setId(USER_ID4);
        user4.setName(USER_NAME4);
        usersUtils.update(user4);

        assertEquals(user1, usersDao.getUser(USER_NAME1));
        assertEquals(user2, usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        assertEquals(user4, usersDao.getUser(USER_NAME4));

        usersUtils.delete(USER_ID1);
        usersUtils.delete(USER_ID4);

        Assert.assertNull(usersDao.getUser(USER_NAME1));
        assertEquals(user2, usersDao.getUser(USER_NAME2));
        assertEquals(user3, usersDao.getUser(USER_NAME3));
        Assert.assertNull(usersDao.getUser(USER_NAME4));

        usersUtils.delete(USER_ID2);
        usersUtils.delete(USER_ID3);

        Assert.assertNull(usersDao.getUser(USER_NAME1));
        Assert.assertNull(usersDao.getUser(USER_NAME2));
        Assert.assertNull(usersDao.getUser(USER_NAME3));
        Assert.assertNull(usersDao.getUser(USER_NAME4));
    }

    private void assertEquals(User expected, User actual) {
        if (expected != actual) {
            Assert.assertEquals(expected.getId(), actual.getId());
            Assert.assertEquals(expected.getName(), actual.getName());
            Assert.assertEquals(expected.getKey(), actual.getKey());
            Assert.assertEquals(expected.getLanguage(), actual.getLanguage());
            Assert.assertEquals(expected.getTimezone(), actual.getTimezone());
        }
    }
}
