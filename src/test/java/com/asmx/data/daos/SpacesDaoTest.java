package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.daos.errors.DataManagementException;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 05.12.15 0:34.
**/
public class SpacesDaoTest extends DaoTestBase {
    @Resource
    private SpacesDao spacesDao;
    @Resource
    private UsersUtils usersUtils;
    @Resource
    private SpacesUtils spacesUtils;
    @Resource
    private ChainsUtils chainsUtils;

    /**
     * See {@link SpacesDao#checkSpaceExists(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsCheckSpaceExists() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID = 123;
        final Space space = spacesUtils.generateInstance();
        space.setId(SPACE_ID);
        spacesUtils.insert(space, user.getId());

        Assert.assertTrue(spacesDao.checkSpaceExists(user, SPACE_ID));

        assertThrows("user is null", () -> spacesDao.checkSpaceExists(null, SPACE_ID));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceExists(user, SPACE_ID));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceExists(user, SPACE_ID));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceExists(user, SPACE_ID));
        user.setId(USER_ID);

        Assert.assertFalse(spacesDao.checkSpaceExists(user, SPACE_ID + 1));
        Assert.assertFalse(spacesDao.checkSpaceExists(user, SPACE_ID + 10));
        Assert.assertFalse(spacesDao.checkSpaceExists(user, SPACE_ID + 100));

        assertThrows("id <= 0", () -> spacesDao.checkSpaceExists(user, 0));
        assertThrows("id <= 0", () -> spacesDao.checkSpaceExists(user, -1));
        assertThrows("id <= 0", () -> spacesDao.checkSpaceExists(user, -10));
    }

    /**
     * See {@link SpacesDao#checkSpaceExists(User, int)}.
    **/
    @Test
    public void testCheckSpaceExists() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        // No insertion

        final int SPACE_ID1 = 123;
        final int SPACE_ID2 = 234;
        final int SPACE_ID3 = 345;
        final int SPACE_ID4 = 456;
        final int SPACE_ID5 = 567;
        final int SPACE_ID6 = 678;
        final int SPACE_ID7 = 789;

        final int spaceIds[] = {SPACE_ID1, SPACE_ID2, SPACE_ID3, SPACE_ID4, SPACE_ID5, SPACE_ID6, SPACE_ID7};

        Set<Integer> spaces1 = new HashSet<>();
        Set<Integer> spaces2 = new HashSet<>();

        for (int spaceId : spaceIds) {
            Assert.assertEquals(spaces1.contains(spaceId), spacesDao.checkSpaceExists(user1, spaceId));
            Assert.assertEquals(spaces2.contains(spaceId), spacesDao.checkSpaceExists(user2, spaceId));
            Assert.assertFalse(spacesDao.checkSpaceExists(user3, spaceId));
        }

        for (int i = 0; i < spaceIds.length; i++) {
            final int newSpaceId = spaceIds[i];

            if (i % 2 == 0) {
                Space space = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
                space.setId(newSpaceId);
                spacesUtils.insert(space, USER_ID1);
                spaces1.add(newSpaceId);
            } else {
                Space space = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
                space.setId(newSpaceId);
                spacesUtils.insert(space, USER_ID2);
                spaces2.add(newSpaceId);
            }

            for (int spaceId : spaceIds) {
                Assert.assertEquals(spaces1.contains(spaceId), spacesDao.checkSpaceExists(user1, spaceId));
                Assert.assertEquals(spaces2.contains(spaceId), spacesDao.checkSpaceExists(user2, spaceId));
                Assert.assertFalse(spacesDao.checkSpaceExists(user3, spaceId));
            }
        }

        for (int i = 0; i < spaceIds.length; i++) {
            final int newSpaceId = spaceIds[i];

            if (i % 2 == 0) {
                Space space = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
                space.setId(newSpaceId);
                spacesUtils.update(space, USER_ID2);
                spaces2.add(newSpaceId);
                spaces1.remove(newSpaceId);
            } else {
                Space space = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
                space.setId(newSpaceId);
                spacesUtils.update(space, USER_ID1);
                spaces1.add(newSpaceId);
                spaces2.remove(newSpaceId);
            }

            for (int spaceId : spaceIds) {
                Assert.assertEquals(spaces1.contains(spaceId), spacesDao.checkSpaceExists(user1, spaceId));
                Assert.assertEquals(spaces2.contains(spaceId), spacesDao.checkSpaceExists(user2, spaceId));
                Assert.assertFalse(spacesDao.checkSpaceExists(user3, spaceId));
            }
        }
    }

    /**
     * See {@link SpacesDao#checkNameInUse(User, String)}.
    **/
    @Test
    public void testInvalidArgumentsCheckNameInUse() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstanceWithUniqueName();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID = 123;
        final Space space = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space.setId(SPACE_ID);
        spacesUtils.insert(space, USER_ID);

        final String nameInUse = space.getName();
        final String nameNotInUse = spacesUtils.generateUniqueName(USER_ID);

        Assert.assertTrue(spacesDao.checkNameInUse(user, nameInUse));
        Assert.assertFalse(spacesDao.checkNameInUse(user, nameNotInUse));

        assertThrows("user is null", () -> spacesDao.checkNameInUse(null, nameInUse));
        assertThrows("user is null", () -> spacesDao.checkNameInUse(null, nameNotInUse));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameInUse));
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameNotInUse));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameInUse));
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameNotInUse));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameInUse));
        assertThrows("user.id <= 0", () -> spacesDao.checkNameInUse(user, nameNotInUse));

        user.setId(USER_ID);

        assertThrows("name is empty", () -> spacesDao.checkNameInUse(user, ""));
        assertThrows("name is blank", () -> spacesDao.checkNameInUse(user, " "));
        assertThrows("name is blank", () -> spacesDao.checkNameInUse(user, "\n"));
        assertThrows("name is too long", () -> spacesDao.checkNameInUse(user, StringUtils.repeat("a", Space.NAME_MAX_LENGTH + 1)));
        assertThrows("name is too long", () -> spacesDao.checkNameInUse(user, StringUtils.repeat("?", Space.NAME_MAX_LENGTH + 10)));
    }

    /**
     * See {@link SpacesDao#checkNameInUse(User, String)}.
    **/
    @Test
    public void testCheckNameInUse() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        // No insertion

        final int count = 10;

        final String[] names = spacesUtils.generateUnique(count, spacesUtils::generateName)
            .toArray(new String[count]);

        for (String name : names) {
            Assert.assertFalse(spacesDao.checkNameInUse(user1, name));
            Assert.assertFalse(spacesDao.checkNameInUse(user2, name));
            Assert.assertFalse(spacesDao.checkNameInUse(user3, name));
        }

        Set<String> names1 = new HashSet<>();
        Set<String> names2 = new HashSet<>();

        for (int i = 0, maxId = 0; i < names.length; i++) {
            final String newName = names[i];

            Space space = spacesUtils.generateInstance();
            space.setName(newName);

            switch (i % 3) {
            case 0:
                space.setId(++maxId);
                spacesUtils.insert(space, USER_ID1);
                names1.add(newName);
                space.setId(++maxId);
                spacesUtils.insert(space, USER_ID2);
                names2.add(newName);
                break;
            case 1:
                space.setId(++maxId);
                spacesUtils.insert(space, USER_ID1);
                names1.add(newName);
                break;
            case 2:
                space.setId(++maxId);
                spacesUtils.insert(space, USER_ID2);
                names2.add(newName);
                break;
            }

            for (String name : names) {
                Assert.assertEquals(names1.contains(name), spacesDao.checkNameInUse(user1, name));
                Assert.assertEquals(names2.contains(name), spacesDao.checkNameInUse(user2, name));
                Assert.assertFalse(spacesDao.checkNameInUse(user3, name));
            }
        }

        for (int i = 0, maxId = 0; i < names.length; i++) {
            final String newName = names[i];

            Space space = spacesUtils.generateInstance();
            space.setName(newName);

            switch (i % 3) {
            case 0:
                spacesUtils.delete(++maxId);
                names1.remove(newName);
                spacesUtils.delete(++maxId);
                names2.remove(newName);
                break;
            case 1:
                space.setId(++maxId);
                spacesUtils.update(space, USER_ID2);
                names2.add(newName);
                names1.remove(newName);
                break;
            case 2:
                space.setId(++maxId);
                spacesUtils.update(space, USER_ID1);
                names1.add(newName);
                names2.remove(newName);
                break;
            }

            for (String name : names) {
                Assert.assertEquals(names1.contains(name), spacesDao.checkNameInUse(user1, name));
                Assert.assertEquals(names2.contains(name), spacesDao.checkNameInUse(user2, name));
                Assert.assertFalse(spacesDao.checkNameInUse(user3, name));
            }
        }
    }

    /**
     * See {@link SpacesDao#checkSpaceInUse(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsCheckSpaceInUse() {
        final int USER_ID = 1234;
        User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstance();
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstance();
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID);

        assertThrows("user is null", () -> spacesDao.checkSpaceInUse(null, SPACE_ID1));
        assertThrows("user is null", () -> spacesDao.checkSpaceInUse(null, SPACE_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID2));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID2));
        user.setId(-123);
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.checkSpaceInUse(user, SPACE_ID2));

        user.setId(USER_ID);

        assertThrows("id <= 0", () -> spacesDao.checkSpaceInUse(user, 0));
        assertThrows("id <= 0", () -> spacesDao.checkSpaceInUse(user, -1));
        assertThrows("id <= 0", () -> spacesDao.checkSpaceInUse(user, -10));
        assertThrows("id <= 0", () -> spacesDao.checkSpaceInUse(user, -123));

        Assert.assertFalse(spacesDao.checkSpaceInUse(user, SPACE_ID1));
        Assert.assertFalse(spacesDao.checkSpaceInUse(user, SPACE_ID2));
    }

    /**
     * See {@link SpacesDao#checkSpaceInUse(User, int)}.
    **/
    @Test
    public void testCheckSpaceInUse() {
        final int USER_ID1 = 1234;
        User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(0);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID1);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(0);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstanceWithUniqueName(0);
        space5.setId(SPACE_ID5);
        spacesUtils.insert(space5, USER_ID2);

        final int SPACE_ID6 = 678;
        final Space space6 = spacesUtils.generateInstanceWithUniqueName(0);
        space6.setId(SPACE_ID6);
        spacesUtils.insert(space6, USER_ID2);

        final int SPACE_ID7 = 789;
        final Space space7 = spacesUtils.generateInstanceWithUniqueName(0);
        space7.setId(SPACE_ID7);
        spacesUtils.insert(space7, USER_ID2);

        Map<Integer, User> spacesOwnership = new HashMap<Integer, User>() {{
            put(SPACE_ID1, user1);
            put(SPACE_ID2, user1);
            put(SPACE_ID3, user1);
            put(SPACE_ID4, user2);
            put(SPACE_ID5, user2);
            put(SPACE_ID6, user2);
            put(SPACE_ID7, user2);
        }};

        Set<Integer> usedIds = new HashSet<>();
        Set<Integer> unusedIds = new HashSet<Integer>() {{
            add(SPACE_ID1);
            add(SPACE_ID2);
            add(SPACE_ID3);
            add(SPACE_ID4);
            add(SPACE_ID5);
            add(SPACE_ID6);
            add(SPACE_ID7);
        }};

        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        final int CHAIN_ID1 = 12;
        chainsUtils.insert(CHAIN_ID1, USER_ID1);
        final int CHAIN_ID2 = 23;
        chainsUtils.insert(CHAIN_ID2, USER_ID1);
        final int CHAIN_ID3 = 34;
        chainsUtils.insert(CHAIN_ID3, USER_ID2);

        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.insertBinding(CHAIN_ID1, SPACE_ID1, USER_ID1);
        unusedIds.remove(SPACE_ID1);
        usedIds.add(SPACE_ID1);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.insertBinding(CHAIN_ID2, SPACE_ID1, USER_ID1);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.insertBinding(CHAIN_ID2, SPACE_ID2, USER_ID1);
        unusedIds.remove(SPACE_ID2);
        usedIds.add(SPACE_ID2);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.insertBinding(CHAIN_ID3, SPACE_ID4, USER_ID2);
        unusedIds.remove(SPACE_ID4);
        usedIds.add(SPACE_ID4);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.deleteBinding(CHAIN_ID1, SPACE_ID1);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.deleteBinding(CHAIN_ID2, SPACE_ID2);
        usedIds.remove(SPACE_ID2);
        unusedIds.add(SPACE_ID2);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.deleteBinding(CHAIN_ID3, SPACE_ID4);
        usedIds.remove(SPACE_ID4);
        unusedIds.add(SPACE_ID4);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);

        chainsUtils.deleteBinding(CHAIN_ID2, SPACE_ID1);
        usedIds.remove(SPACE_ID1);
        unusedIds.add(SPACE_ID1);
        assertSpacesInUse(unusedIds, usedIds, spacesOwnership);
    }

    private void assertSpacesInUse(Set<Integer> unusedIds, Set<Integer> usedIds, Map<Integer, User> ownership) {
        Set<User> users = new HashSet<>(ownership.values());
        for (int spaceId : unusedIds) {
            for (User user : users) {
                Assert.assertFalse(spacesDao.checkSpaceInUse(user, spaceId));
            }
        }

        for (int id : usedIds) {
            User owner = ownership.get(id);
            for (User user : users) {
                if (user == owner) {
                    Assert.assertTrue(spacesDao.checkSpaceInUse(user, id));
                } else {
                    Assert.assertFalse(spacesDao.checkSpaceInUse(user, id));
                }
            }
        }
    }

    /**
     * See {@link SpacesDao#createSpace(User, Space)}.
    **/
    @Test
    public void testInvalidArgumentsCreateSpace() {
        assertQuery("SELECT COUNT(*) = 0 FROM spaces");

        final int USER_ID = 1234;
        User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        Space space1 = spacesUtils.generateInstance();
        assertThrows("user is null", () -> spacesDao.createSpace(null, space1));

        Space space2 = spacesUtils.generateInstance();
        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.createSpace(user, space2));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.createSpace(user, space2));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> spacesDao.createSpace(user, space2));

        user.setId(USER_ID);

        assertThrows("space is null", () -> spacesDao.createSpace(user, null));

        Space space3 = spacesUtils.generateInstance();
        space3.setName(null);
        assertThrows("name is null", () -> spacesDao.createSpace(user, space3));
        space3.setName("");
        assertThrows("name is empty", () -> spacesDao.createSpace(user, space3));
        space3.setName(" ");
        assertThrows("name is blank", () -> spacesDao.createSpace(user, space3));
        space3.setName("\n");
        assertThrows("name is blank", () -> spacesDao.createSpace(user, space3));
        space3.setName(StringUtils.repeat("a", Space.NAME_MAX_LENGTH + 1));
        assertThrows("name is too long", () -> spacesDao.createSpace(user, space3));
        space3.setName(StringUtils.repeat("?", Space.NAME_MAX_LENGTH + 10));
        assertThrows("name is too long", () -> spacesDao.createSpace(user, space3));

        Space space4 = spacesUtils.generateInstance();
        space4.setDescription(null);
        assertThrows("description is null", () -> spacesDao.createSpace(user, space4));

        Space space5 = spacesUtils.generateInstance();
        space5.setCreationTime(null);
        assertThrows("creation time is null", () -> spacesDao.createSpace(user, space5));

        assertQuery("SELECT COUNT(*) = 0 FROM spaces");

        Space space6 = spacesUtils.generateInstance();
        Assert.assertTrue(spacesDao.createSpace(user, space6) > 0);

        assertQuery("SELECT COUNT(*) = 1 FROM spaces");
    }

    /**
     * See {@link SpacesDao#createSpace(User, Space)}.
    **/
    @Test
    public void testConstraintsCreateSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        // No insertion

        final int USER_ID4 = 4567;
        final User user4 = usersUtils.generateInstanceWithUniqueName();
        user4.setId(USER_ID4);
        // No insertion

        Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        spacesDao.createSpace(user1, space1);

        Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        spacesDao.createSpace(user2, space2);

        assertQuery("SELECT COUNT(*) = 2 FROM spaces");

        Space space3 = spacesUtils.generateInstanceWithUniqueName(0);
        assertThrows("user doesn't exist", () -> spacesDao.createSpace(user3, space3), DataAccessException.class);

        Space space4 = spacesUtils.generateInstanceWithUniqueName(0);
        assertThrows("user doesn't exist", () -> spacesDao.createSpace(user4, space4), DataAccessException.class);

        assertQuery("SELECT COUNT(*) = 2 FROM spaces");

        spacesDao.createSpace(user1, space3);
        spacesDao.createSpace(user2, space4);

        assertQuery("SELECT COUNT(*) = 4 FROM spaces");

        spacesDao.createSpace(user2, space1);
        spacesDao.createSpace(user1, space2);
        spacesDao.createSpace(user2, space3);
        spacesDao.createSpace(user1, space4);

        assertQuery("SELECT COUNT(*) = 8 FROM spaces");

        assertQuery("SELECT COUNT(*) = 4 FROM spaces WHERE user_id = ?", USER_ID1);
        assertQuery("SELECT COUNT(*) = 4 FROM spaces WHERE user_id = ?", USER_ID2);

        assertQuery("SELECT COUNT(*) = 2 FROM spaces WHERE name = ?", space1.getName());
        assertQuery("SELECT COUNT(*) = 2 FROM spaces WHERE name = ?", space2.getName());
        assertQuery("SELECT COUNT(*) = 2 FROM spaces WHERE name = ?", space3.getName());
        assertQuery("SELECT COUNT(*) = 2 FROM spaces WHERE name = ?", space4.getName());

        Space space5 = spacesUtils.generateInstance();
        space5.setName(space1.getName());
        assertThrows("name duplication", () -> spacesDao.createSpace(user1, space5), DataAccessException.class);
        assertThrows("name duplication", () -> spacesDao.createSpace(user2, space5), DataAccessException.class);

        Space space6 = spacesUtils.generateInstance();
        space6.setName(space2.getName());
        assertThrows("name duplication", () -> spacesDao.createSpace(user1, space6), DataAccessException.class);
        assertThrows("name duplication", () -> spacesDao.createSpace(user2, space6), DataAccessException.class);

        Space space7 = spacesUtils.generateInstance();
        space7.setName(space3.getName());
        assertThrows("name duplication", () -> spacesDao.createSpace(user1, space7), DataAccessException.class);
        assertThrows("name duplication", () -> spacesDao.createSpace(user2, space7), DataAccessException.class);

        Space space8 = spacesUtils.generateInstance();
        space8.setName(space4.getName());
        assertThrows("name duplication", () -> spacesDao.createSpace(user1, space8), DataAccessException.class);
        assertThrows("name duplication", () -> spacesDao.createSpace(user2, space8), DataAccessException.class);

        assertQuery("SELECT COUNT(*) = 8 FROM spaces");
    }

    /**
     * See {@link SpacesDao#createSpace(User, Space)}.
    **/
    @Test
    public void testCreateSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int count = 15;

        final String[] names = spacesUtils.generateUnique(count, spacesUtils::generateName)
            .toArray(new String[count]);

        assertQuery("SELECT COUNT(*) = 0 FROM spaces");

        Map<String, Space> spaceMap = new HashMap<>();

        for (int i = 0; i < count; i++) {
            final String newName = names[i];

            Space space = spacesUtils.generateInstance();
            space.setName(newName);

            int userId;
            int spaceId;

            if (i % 2 == 0) {
                userId = USER_ID1;
                spaceId = spacesDao.createSpace(user1, space);
            } else {
                userId = USER_ID2;
                spaceId = spacesDao.createSpace(user2, space);
            }

            spaceMap.put(newName, space);
            assertEquals(space, spacesUtils.select(spaceId, userId));

            assertQuery("SELECT COUNT(*) = ? FROM spaces", i + 1);
        }

        for (int i = 0; i < count; i++) {
            final String name = names[i];
            final int userId = (i % 2 == 0) ? USER_ID1 : USER_ID2;
            Space space = spaceMap.get(name);
            assertEquals(space, spacesUtils.select(space.getId(), userId));
        }
    }

    /**
     * See {@link SpacesDao#changeSpace(User, int, String, String)}.
    **/
    @Test
    public void testInvalidArgumentsChangeSpace() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID = 123;
        final Space space = spacesUtils.generateInstance();
        space.setId(SPACE_ID);
        spacesUtils.insert(space, USER_ID);

        final String newName = "This is a new (altered) name";
        final String newDescription = "This is a new (altered) description";

        assertThrows("user is null", () -> spacesDao.changeSpace(null, SPACE_ID, newName, newDescription));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.changeSpace(user, SPACE_ID, newName, newDescription));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.changeSpace(user, SPACE_ID, newName, newDescription));
        user.setId(-1234);
        assertThrows("user.id <= 0", () -> spacesDao.changeSpace(user, SPACE_ID, newName, newDescription));

        user.setId(USER_ID);

        assertThrows("id <= 0", () -> spacesDao.changeSpace(user, 0, newName, newDescription));
        assertThrows("id <= 0", () -> spacesDao.changeSpace(user, -1, newName, newDescription));
        assertThrows("id <= 0", () -> spacesDao.changeSpace(user, -5, newName, newDescription));

        assertThrows("name is null", () -> spacesDao.changeSpace(user, SPACE_ID, null, newDescription));
        assertThrows("name is blank", () -> spacesDao.changeSpace(user, SPACE_ID, "", newDescription));
        assertThrows("name is blank", () -> spacesDao.changeSpace(user, SPACE_ID, "  ", newDescription));

        assertThrows("name is too long", () -> spacesDao.changeSpace(user, SPACE_ID, StringUtils.repeat("a", Space.NAME_MAX_LENGTH + 1), newDescription));
        assertThrows("name is too long", () -> spacesDao.changeSpace(user, SPACE_ID, StringUtils.repeat("?", Space.NAME_MAX_LENGTH + 5), newDescription));

        assertThrows("description is null", () -> spacesDao.changeSpace(user, SPACE_ID, newName, null));

        assertThrows("space id doesn't exist", () -> spacesDao.changeSpace(user, SPACE_ID + 1, newName, newDescription), DataManagementException.class);
        assertThrows("space id doesn't exist", () -> spacesDao.changeSpace(user, SPACE_ID + 5, newName, newDescription), DataManagementException.class);

        assertEquals(spacesUtils.select(SPACE_ID, USER_ID), space);

        spacesDao.changeSpace(user, SPACE_ID, newName, newDescription);
        space.setName(newName);
        space.setDescription(newDescription);
        assertEquals(spacesUtils.select(SPACE_ID, USER_ID), space);
    }

    /**
     * See {@link SpacesDao#changeSpace(User, int, String, String)}.
    **/
    @Test
    public void testConstraintsChangeSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        // No insertion

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(0);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID1);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(0);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstanceWithUniqueName(0);
        space5.setId(SPACE_ID5);
        spacesUtils.insert(space5, USER_ID2);

        final int SPACE_ID6 = 678;
        final Space space6 = spacesUtils.generateInstanceWithUniqueName(0);
        space6.setId(SPACE_ID6);
        spacesUtils.insert(space6, USER_ID2);

        String name = spacesUtils.generateUniqueName(0);
        String description = spacesUtils.generateDescription();

        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user2, SPACE_ID1, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user2, SPACE_ID2, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user2, SPACE_ID3, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID1, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID2, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID3, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user1, SPACE_ID4, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user1, SPACE_ID5, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user1, SPACE_ID6, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID4, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID5, name, description), DataManagementException.class);
        assertThrows("user/space doesn't exist", () -> spacesDao.changeSpace(user3, SPACE_ID6, name, description), DataManagementException.class);

        assertEquals(spacesUtils.select(SPACE_ID1, USER_ID1), space1);
        assertEquals(spacesUtils.select(SPACE_ID2, USER_ID1), space2);
        assertEquals(spacesUtils.select(SPACE_ID3, USER_ID1), space3);
        assertEquals(spacesUtils.select(SPACE_ID4, USER_ID2), space4);
        assertEquals(spacesUtils.select(SPACE_ID5, USER_ID2), space5);
        assertEquals(spacesUtils.select(SPACE_ID6, USER_ID2), space6);

        exchangeNameAndDescription(space1, space4);
        exchangeNameAndDescription(space2, space5);
        exchangeNameAndDescription(space3, space6);

        spacesDao.changeSpace(user1, SPACE_ID1, space1.getName(), space1.getDescription());
        spacesDao.changeSpace(user1, SPACE_ID2, space2.getName(), space2.getDescription());
        spacesDao.changeSpace(user1, SPACE_ID3, space3.getName(), space3.getDescription());

        spacesDao.changeSpace(user2, SPACE_ID4, space4.getName(), space4.getDescription());
        spacesDao.changeSpace(user2, SPACE_ID5, space5.getName(), space5.getDescription());
        spacesDao.changeSpace(user2, SPACE_ID6, space6.getName(), space6.getDescription());

        assertEquals(spacesUtils.select(SPACE_ID1, USER_ID1), space1);
        assertEquals(spacesUtils.select(SPACE_ID2, USER_ID1), space2);
        assertEquals(spacesUtils.select(SPACE_ID3, USER_ID1), space3);
        assertEquals(spacesUtils.select(SPACE_ID4, USER_ID2), space4);
        assertEquals(spacesUtils.select(SPACE_ID5, USER_ID2), space5);
        assertEquals(spacesUtils.select(SPACE_ID6, USER_ID2), space6);

        final Space spaces1[] = {space1, space2, space3};
        for (int i = 0; i < spaces1.length; i++) {
            for (int k = 0; k < spaces1.length; k++) {
                Space s1 = spaces1[i], s2 = spaces1[k];
                if (i != k) {
                    assertThrows("name duplication", () -> spacesDao.changeSpace(user1, s1.getId(), s2.getName(), s2.getDescription()), DataAccessException.class);
                }
            }
        }

        final Space spaces2[] = {space4, space5, space6};
        for (int i = 0; i < spaces2.length; i++) {
            for (int k = 0; k < spaces2.length; k++) {
                Space s1 = spaces2[i], s2 = spaces2[k];
                if (i != k) {
                    assertThrows("name duplication", () -> spacesDao.changeSpace(user1, s1.getId(), s2.getName(), s2.getDescription()), DataAccessException.class);
                }
            }
        }

        assertEquals(spacesUtils.select(SPACE_ID1, USER_ID1), space1);
        assertEquals(spacesUtils.select(SPACE_ID2, USER_ID1), space2);
        assertEquals(spacesUtils.select(SPACE_ID3, USER_ID1), space3);
        assertEquals(spacesUtils.select(SPACE_ID4, USER_ID2), space4);
        assertEquals(spacesUtils.select(SPACE_ID5, USER_ID2), space5);
        assertEquals(spacesUtils.select(SPACE_ID6, USER_ID2), space6);

        assertQuery("SELECT COUNT(*) = 3 FROM spaces WHERE user_id = ?", USER_ID1);
        assertQuery("SELECT COUNT(*) = 3 FROM spaces WHERE user_id = ?", USER_ID2);
        assertQuery("SELECT COUNT(*) = 6 FROM spaces");
    }

    /**
     * See {@link SpacesDao#changeSpace(User, int, String, String)}.
    **/
    @Test
    public void testChangeSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(0);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID1);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(0);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstanceWithUniqueName(0);
        space5.setId(SPACE_ID5);
        spacesUtils.insert(space5, USER_ID2);

        final int SPACE_ID6 = 678;
        final Space space6 = spacesUtils.generateInstanceWithUniqueName(0);
        space6.setId(SPACE_ID6);
        spacesUtils.insert(space6, USER_ID2);

        final UserSpace spaces[] = new UserSpace[] {
            new UserSpace(space1, user1),
            new UserSpace(space2, user1),
            new UserSpace(space3, user1),
            new UserSpace(space4, user2),
            new UserSpace(space5, user2),
            new UserSpace(space6, user2)
        };

        final int count = 20;
        for (int i = 0; i < count; i++) {
            UserSpace us = spaces[random.nextInt(spaces.length)];

            String newName = spacesUtils.generateUniqueName(0);
            String newDescription = spacesUtils.generateDescription();

            us.space.setName(newName);
            us.space.setDescription(newDescription);

            spacesDao.changeSpace(us.user, us.space.getId(), newName, newDescription);

            assertEquals(spacesUtils.select(us.space.getId(), us.user.getId()), us.space);
        }

        for (UserSpace us : spaces) {
            assertEquals(spacesUtils.select(us.space.getId(), us.user.getId()), us.space);
        }
    }

    /**
     * See {@link SpacesDao#deleteSpace(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsDeleteSpace() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID);

        assertThrows("user is null", () -> spacesDao.deleteSpace(null, SPACE_ID1));
        assertThrows("user is null", () -> spacesDao.deleteSpace(null, SPACE_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID2));

        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID2));

        user.setId(-100);
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID1));
        assertThrows("user.id <= 0", () -> spacesDao.deleteSpace(user, SPACE_ID2));

        user.setId(USER_ID);
        assertThrows("id <= 0", () -> spacesDao.deleteSpace(user, 0));
        assertThrows("id <= 0", () -> spacesDao.deleteSpace(user, -1));
        assertThrows("id <= 0", () -> spacesDao.deleteSpace(user, -10));
        assertThrows("id <= 0", () -> spacesDao.deleteSpace(user, -123));

        assertQuery("SELECT COUNT(*) = 2 FROM spaces");
        assertQuery("SELECT COUNT(*) = 1 FROM spaces WHERE user_id = ? AND id = ?", USER_ID, SPACE_ID1);
        assertQuery("SELECT COUNT(*) = 1 FROM spaces WHERE user_id = ? AND id = ?", USER_ID, SPACE_ID2);

        assertEquals(space1, spacesUtils.select(SPACE_ID1, USER_ID));
        assertEquals(space2, spacesUtils.select(SPACE_ID2, USER_ID));

        Assert.assertTrue(spacesDao.deleteSpace(user, SPACE_ID1));
        Assert.assertTrue(spacesDao.deleteSpace(user, SPACE_ID2));

        assertQuery("SELECT COUNT(*) = 0 FROM spaces");
    }

    /**
     * See {@link SpacesDao#deleteSpace(User, int)}.
    **/
    @Test
    public void testDeleteSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        usersUtils.insert(user3);

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(0);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID1);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(0);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstanceWithUniqueName(0);
        space5.setId(SPACE_ID5);
        spacesUtils.insert(space5, USER_ID2);

        final int SPACE_ID6 = 678;
        final Space space6 = spacesUtils.generateInstanceWithUniqueName(0);
        space6.setId(SPACE_ID6);
        spacesUtils.insert(space6, USER_ID2);

        Set<Integer> spaces1 = new HashSet<Integer>() {{
            add(SPACE_ID1);
            add(SPACE_ID2);
            add(SPACE_ID3);
        }};

        Set<Integer> spaces2 = new HashSet<Integer>() {{
            add(SPACE_ID4);
            add(SPACE_ID5);
            add(SPACE_ID6);
        }};

        assertQuery("SELECT COUNT(*) = ? FROM spaces WHERE user_id = ?", spaces1.size(), USER_ID1);
        assertQuery("SELECT COUNT(*) = ? FROM spaces WHERE user_id = ?", spaces2.size(), USER_ID2);
        assertQuery("SELECT COUNT(*) = 0 FROM spaces WHERE user_id = ?", USER_ID3);

        for (int spaceId : spaces1) {
            Assert.assertFalse(spacesDao.deleteSpace(user2, spaceId));
            Assert.assertFalse(spacesDao.deleteSpace(user3, spaceId));
        }

        for (int spaceId : spaces2) {
            Assert.assertFalse(spacesDao.deleteSpace(user1, spaceId));
            Assert.assertFalse(spacesDao.deleteSpace(user3, spaceId));
        }

        assertQuery("SELECT COUNT(*) = ? FROM spaces WHERE user_id = ?", spaces1.size(), USER_ID1);
        assertQuery("SELECT COUNT(*) = ? FROM spaces WHERE user_id = ?", spaces2.size(), USER_ID2);
        assertQuery("SELECT COUNT(*) = 0 FROM spaces WHERE user_id = ?", USER_ID3);

        Set<Integer> deleted = new HashSet<>();
        Set<Integer> existing = new HashSet<Integer>() {{
            addAll(spaces1);
            addAll(spaces2);
        }};

        assertQuery("SELECT COUNT(*) = ? FROM spaces", existing.size());

        while (!existing.isEmpty()) {
            final int spaceId = spacesUtils.any(existing);
            final User user = spaces1.contains(spaceId) ? user1 : user2;

            existing.remove(spaceId);
            deleted.add(spaceId);

            assertQuery("SELECT COUNT(*) = 1 FROM spaces WHERE id = ?", spaceId);

            Assert.assertTrue(spacesDao.deleteSpace(user, spaceId));

            for (int id : existing) {
                assertQuery("SELECT COUNT(*) = 1 FROM spaces WHERE id = ?", id);
            }

            for (int id : deleted) {
                assertQuery("SELECT COUNT(*) = 0 FROM spaces WHERE id = ?", id);
            }

            Assert.assertFalse(spacesDao.deleteSpace(user, spaceId));
        }

        assertQuery("SELECT COUNT(*) = 0 FROM spaces");
    }

    /**
     * See {@link SpacesDao#getSpaces(User, Sorting)}.
    **/
    @Test
    public void testInvalidArgumentsGetSpaces() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final Sorting sorting = Sorting.ascending("id");
        List<Space> spaces;

        spaces = spacesDao.getSpaces(user, null);
        Assert.assertNotNull(spaces);
        Assert.assertTrue(spaces.isEmpty());

        spaces = spacesDao.getSpaces(user, sorting);
        Assert.assertNotNull(spaces);
        Assert.assertTrue(spaces.isEmpty());

        assertThrows("user is null", () -> spacesDao.getSpaces(null, null));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, null));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, null));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, null));

        assertThrows("user is null", () -> spacesDao.getSpaces(null, sorting));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, sorting));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, sorting));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> spacesDao.getSpaces(user, sorting));
    }

    /**
     * See {@link SpacesDao#getSpaces(User, Sorting)}.
    **/
    @Test
    public void testGetSpaces() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        assertEquals(Collections.emptyList(), spacesDao.getSpaces(user1, null));
        assertEquals(Collections.emptyList(), spacesDao.getSpaces(user2, null));

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstance();
        space1.setId(SPACE_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstance();
        space2.setId(SPACE_ID2);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstance();
        space3.setId(SPACE_ID3);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstance();
        space4.setId(SPACE_ID4);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstance();
        space5.setId(SPACE_ID5);

        final int SPACE_ID6 = 678;
        final Space space6 = spacesUtils.generateInstance();
        space6.setId(SPACE_ID6);

        final int SPACE_ID7 = 789;
        final Space space7 = spacesUtils.generateInstance();
        space7.setId(SPACE_ID7);

        space2.setName("a");
        space5.setName("b-0");
        space3.setName("b-9");
        space1.setName("c");
        space4.setName("z");

        space7.setName("x:n");
        space6.setName("x:m");

        space3.setDescription("A");
        space2.setDescription("B");
        space4.setDescription("C-1");
        space1.setDescription("C-9");
        space5.setDescription("D");

        space6.setDescription("description/x");
        space7.setDescription("description/z");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        space3.setCreationTime(calendar.getTime());
        calendar.add(Calendar.MINUTE, 2);
        space5.setCreationTime(calendar.getTime());
        calendar.add(Calendar.HOUR, 3);
        space4.setCreationTime(calendar.getTime());
        calendar.add(Calendar.DATE, 4);
        space1.setCreationTime(calendar.getTime());
        calendar.add(Calendar.YEAR, 5);
        space2.setCreationTime(calendar.getTime());

        space7.setCreationTime(calendar.getTime());
        calendar.add(Calendar.SECOND, 1);
        space6.setCreationTime(calendar.getTime());

        spacesUtils.insert(space5, USER_ID1);
        spacesUtils.insert(space2, USER_ID1);
        spacesUtils.insert(space3, USER_ID1);
        spacesUtils.insert(space1, USER_ID1);
        spacesUtils.insert(space4, USER_ID1);

        spacesUtils.insert(space7, USER_ID2);
        spacesUtils.insert(space6, USER_ID2);

        Sorting sorting;

        sorting = Sorting.ascending("id");
        assertEquals(Arrays.asList(space1, space2, space3, space4, space5), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("id");
        assertEquals(Arrays.asList(space5, space4, space3, space2, space1), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.ascending("name");
        assertEquals(Arrays.asList(space2, space5, space3, space1, space4), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("name");
        assertEquals(Arrays.asList(space4, space1, space3, space5, space2), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.ascending("description");
        assertEquals(Arrays.asList(space3, space2, space4, space1, space5), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("description");
        assertEquals(Arrays.asList(space5, space1, space4, space2, space3), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.ascending("creation_time");
        assertEquals(Arrays.asList(space3, space5, space4, space1, space2), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("creation_time");
        assertEquals(Arrays.asList(space2, space1, space4, space5, space3), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));

        space3.setDescription("#A");
        space5.setDescription("#A");
        space4.setDescription("#B");
        space1.setDescription("#X");
        space2.setDescription("#X");

        spacesUtils.update(space1, USER_ID1);
        spacesUtils.update(space2, USER_ID1);
        spacesUtils.update(space3, USER_ID1);
        spacesUtils.update(space4, USER_ID1);
        spacesUtils.update(space5, USER_ID1);

        sorting = Sorting.ascending("description");
        assertEquals(Arrays.asList(space3, space5, space4, space1, space2), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("description");
        assertEquals(Arrays.asList(space1, space2, space4, space3, space5), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        calendar.add(Calendar.SECOND, 1);
        space2.setCreationTime(calendar.getTime());
        space4.setCreationTime(calendar.getTime());
        calendar.add(Calendar.SECOND, 1);
        space1.setCreationTime(calendar.getTime());
        space3.setCreationTime(calendar.getTime());
        space5.setCreationTime(calendar.getTime());

        spacesUtils.update(space1, USER_ID1);
        spacesUtils.update(space2, USER_ID1);
        spacesUtils.update(space3, USER_ID1);
        spacesUtils.update(space4, USER_ID1);
        spacesUtils.update(space5, USER_ID1);

        sorting = Sorting.ascending("creation_time");
        assertEquals(Arrays.asList(space2, space4, space1, space3, space5), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space7, space6), spacesDao.getSpaces(user2, sorting));

        sorting = Sorting.descending("creation_time");
        assertEquals(Arrays.asList(space1, space3, space5, space2, space4), spacesDao.getSpaces(user1, sorting));
        assertEquals(Arrays.asList(space6, space7), spacesDao.getSpaces(user2, sorting));
    }

    /**
     * See {@link SpacesDao#getSpace(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsGetSpace() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID = 123;
        final Space space = spacesUtils.generateInstance();
        space.setId(SPACE_ID);
        spacesUtils.insert(space, USER_ID);

        assertEquals(space, spacesDao.getSpace(user, SPACE_ID));

        assertThrows("user is null", () -> spacesDao.getSpace(null, SPACE_ID));

        user.setId(0);
        assertThrows("user.id <= 0", () -> spacesDao.getSpace(user, SPACE_ID));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> spacesDao.getSpace(user, SPACE_ID));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> spacesDao.getSpace(user, SPACE_ID));
        user.setId(1);
        Assert.assertNull(spacesDao.getSpace(user, SPACE_ID));

        user.setId(USER_ID);

        Assert.assertNull(spacesDao.getSpace(user, SPACE_ID + 1));
        assertThrows("id <= 0", () -> spacesDao.getSpace(user, 0));
        assertThrows("id <= 0", () -> spacesDao.getSpace(user, -1));
        assertThrows("id <= 0", () -> spacesDao.getSpace(user, -10));
    }

    /**
     * See {@link SpacesDao#getSpace(User, int)}.
    **/
    @Test
    public void testGetSpace() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        user1.setName(usersUtils.generateUniqueName());
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        user2.setName(usersUtils.generateUniqueName());
        usersUtils.insert(user2);

        final int USER_ID3 = 3456;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        user3.setName(usersUtils.generateUniqueName());

        final int SPACE_ID1 = 123;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space1.setId(SPACE_ID1);

        final int SPACE_ID2 = 234;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space2.setId(SPACE_ID2);

        final int SPACE_ID3 = 345;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space3.setId(SPACE_ID3);

        final int SPACE_ID4 = 456;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space4.setId(SPACE_ID4);

        final int SPACE_ID5 = 567;
        final Space space5 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space5.setId(SPACE_ID5);

        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID5));

        spacesUtils.insert(space1, USER_ID1);
        spacesUtils.insert(space2, USER_ID2);

        assertEquals(space1, spacesDao.getSpace(user1, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID1));
        assertEquals(space2, spacesDao.getSpace(user2, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID5));

        spacesUtils.insert(space3, USER_ID1);
        spacesUtils.insert(space4, USER_ID2);
        spacesUtils.insert(space5, USER_ID2);

        assertEquals(space1, spacesDao.getSpace(user1, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID2));
        assertEquals(space3, spacesDao.getSpace(user1, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user1, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID1));
        assertEquals(space2, spacesDao.getSpace(user2, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user2, SPACE_ID3));
        assertEquals(space4, spacesDao.getSpace(user2, SPACE_ID4));
        assertEquals(space5, spacesDao.getSpace(user2, SPACE_ID5));

        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID1));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID2));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID3));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID4));
        Assert.assertNull(spacesDao.getSpace(user3, SPACE_ID5));
    }

    private void exchangeNameAndDescription(Space s1, Space s2) {
        String v;

        v = s1.getName();
        s1.setName(s2.getName());
        s2.setName(v);

        v = s1.getDescription();
        s1.setDescription(s2.getDescription());
        s2.setDescription(v);
    }

    private void assertEquals(List<Space> expected, List<Space> actual) {
        if (expected == null) {
            Assert.assertNull(actual);
        } else {
            Assert.assertNotNull(actual);
            Assert.assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), actual.get(i));
            }
        }
    }

    private void assertEquals(Space expected, Space actual) {
        if (expected != actual) {
            Assert.assertNotNull(actual);
            Assert.assertEquals(expected.getId(), actual.getId());
            Assert.assertEquals(expected.getName(), actual.getName());
            Assert.assertEquals(expected.getDescription(), actual.getDescription());
            Assert.assertEquals(expected.getCreationTime(), actual.getCreationTime());
        }
    }

    private static class UserSpace {
        public Space space;
        public User user;

        public UserSpace(Space space, User user) {
            this.space = space;
            this.user = user;
        }
    }
}
