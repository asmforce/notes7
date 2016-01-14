package com.asmx.data.daos;

import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: asmforce
 * Timestamp: 08.01.16 18:13.
**/
public class ChainsDaoTest extends DaoTestBase {
    @Resource
    private ChainsDao chainsDao;
    @Resource
    private UsersUtils usersUtils;
    @Resource
    private SpacesUtils spacesUtils;
    @Resource
    private ChainsUtils chainsUtils;

    /**
     * See {@link ChainsDao#checkChainExists(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsCheckChainExists() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int CHAIN_ID1 = 123;
        chainsUtils.insert(CHAIN_ID1, USER_ID);
        final int CHAIN_ID2 = 234;
        chainsUtils.insert(CHAIN_ID2, USER_ID);

        Assert.assertTrue(chainsDao.checkChainExists(user, CHAIN_ID1));
        Assert.assertTrue(chainsDao.checkChainExists(user, CHAIN_ID2));

        assertThrows("user is null", () -> chainsDao.checkChainExists(null, CHAIN_ID1));
        assertThrows("user is null", () -> chainsDao.checkChainExists(null, CHAIN_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID2));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID2));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID2));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.checkChainExists(user, CHAIN_ID2));

        user.setId(USER_ID);
        assertThrows("id <= 0", () -> chainsDao.checkChainExists(user, 0));
        assertThrows("id <= 0", () -> chainsDao.checkChainExists(user, -1));
        assertThrows("id <= 0", () -> chainsDao.checkChainExists(user, -12));
        assertThrows("id <= 0", () -> chainsDao.checkChainExists(user, -123));
    }

    /**
     * See {@link ChainsDao#checkChainExists(User, int)}.
    **/
    @Test
    public void testCheckChainExists() {
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

        final int CHAIN_ID1 = 123;
        final int CHAIN_ID2 = 234;
        final int CHAIN_ID3 = 345;
        final int CHAIN_ID4 = 456;
        final int CHAIN_ID5 = 567;
        final int CHAIN_ID6 = 678;
        final int CHAIN_ID7 = 789;

        final int chainIds[] = {CHAIN_ID1, CHAIN_ID2, CHAIN_ID3, CHAIN_ID4, CHAIN_ID5, CHAIN_ID6, CHAIN_ID7};

        Set<Integer> chains1 = new HashSet<>();
        Set<Integer> chains2 = new HashSet<>();

        for (int chainId : chainIds) {
            Assert.assertEquals(chains1.contains(chainId), chainsDao.checkChainExists(user1, chainId));
            Assert.assertEquals(chains2.contains(chainId), chainsDao.checkChainExists(user2, chainId));
            Assert.assertFalse(chainsDao.checkChainExists(user3, chainId));
        }

        for (int i = 0; i < chainIds.length; i++) {
            final int newChainId = chainIds[i];

            if (i % 2 == 0) {
                chainsUtils.insert(newChainId, USER_ID1);
                chains1.add(newChainId);
            } else {
                chainsUtils.insert(newChainId, USER_ID2);
                chains2.add(newChainId);
            }

            for (int chainId : chainIds) {
                Assert.assertEquals(chains1.contains(chainId), chainsDao.checkChainExists(user1, chainId));
                Assert.assertEquals(chains2.contains(chainId), chainsDao.checkChainExists(user2, chainId));
                Assert.assertFalse(chainsDao.checkChainExists(user3, chainId));
            }
        }

        for (int i = 0; i < chainIds.length; i++) {
            final int newChainId = chainIds[i];

            if (i % 2 == 0) {
                chainsUtils.update(newChainId, USER_ID2);
                chains2.add(newChainId);
                chains1.remove(newChainId);
            } else {
                chainsUtils.update(newChainId, USER_ID1);
                chains1.add(newChainId);
                chains2.remove(newChainId);
            }

            for (int chainId : chainIds) {
                Assert.assertEquals(chains1.contains(chainId), chainsDao.checkChainExists(user1, chainId));
                Assert.assertEquals(chains2.contains(chainId), chainsDao.checkChainExists(user2, chainId));
                Assert.assertFalse(chainsDao.checkChainExists(user3, chainId));
            }
        }
    }

    /**
     * See {@link ChainsDao#createChain(User)}.
    **/
    @Test
    public void testInvalidArgumentsCreateChain() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        assertThrows("user is null", () -> chainsDao.createChain(null));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.createChain(user));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.createChain(user));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.createChain(user));
        user.setId(-123);
        assertThrows("user.id <= 0", () -> chainsDao.createChain(user));

        user.setId(USER_ID);
        Assert.assertTrue(chainsDao.createChain(user) > 0);

        assertQuery("SELECT COUNT(*) = 1 FROM chains");
    }

    /**
     * See {@link ChainsDao#createChain(User)}.
    **/
    @Test
    public void testConstraintsCreateChain() {
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

        Assert.assertTrue(chainsDao.createChain(user1) > 0);
        Assert.assertTrue(chainsDao.createChain(user2) > 0);
        assertThrows("user doesn't exist", () -> chainsDao.createChain(user3), DataAccessException.class);

        Assert.assertTrue(chainsDao.createChain(user1) > 0);
        Assert.assertTrue(chainsDao.createChain(user2) > 0);
        assertThrows("user doesn't exist", () -> chainsDao.createChain(user3), DataAccessException.class);

        assertQuery("SELECT COUNT(*) = 4 FROM chains");
        assertQuery("SELECT COUNT(*) = 2 FROM chains WHERE user_id = ?", USER_ID1);
        assertQuery("SELECT COUNT(*) = 2 FROM chains WHERE user_id = ?", USER_ID2);
        assertQuery("SELECT COUNT(*) = 0 FROM chains WHERE user_id = ?", USER_ID3);
    }

    /**
     * See {@link ChainsDao#createChain(User)}.
    **/
    @Test
    public void testCreateChain() {
        final int USER_ID1 = 1234;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 2345;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        Set<Integer> chains = new HashSet<>();
        Set<Integer> chains1 = new HashSet<>();
        Set<Integer> chains2 = new HashSet<>();

        final int count = 20;

        for (int i = 1; i <= count; i++) {
            int newChainId;
            if (random.nextBoolean()) {
                newChainId = chainsDao.createChain(user1);
                chains1.add(newChainId);
            } else {
                newChainId = chainsDao.createChain(user2);
                chains2.add(newChainId);
            }

            Assert.assertFalse(chains.contains(newChainId));
            chains.add(newChainId);

            for (int chainId : chains1) {
                Assert.assertTrue(chainsUtils.exists(chainId, USER_ID1));
                Assert.assertFalse(chainsUtils.exists(chainId, USER_ID2));
            }

            for (int chainId : chains2) {
                Assert.assertTrue(chainsUtils.exists(chainId, USER_ID2));
                Assert.assertFalse(chainsUtils.exists(chainId, USER_ID1));
            }
        }
    }

    /**
     * See {@link ChainsDao#deleteChain(User, int)}.
    **/
    @Test
    public void testInvalidArgumentsDeleteChain() {
        final int USER_ID = 1234;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int CHAIN_ID1 = 123;
        final int CHAIN_ID2 = 234;
        final int CHAIN_ID3 = 345;

        chainsUtils.insert(CHAIN_ID1, USER_ID);
        chainsUtils.insert(CHAIN_ID2, USER_ID);
        chainsUtils.insert(CHAIN_ID3, USER_ID);

        assertThrows("user is null", () -> chainsDao.deleteChain(null, CHAIN_ID1));
        assertThrows("user is null", () -> chainsDao.deleteChain(null, CHAIN_ID2));
        assertThrows("user is null", () -> chainsDao.deleteChain(null, CHAIN_ID3));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID2));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID3));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID2));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID3));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID2));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChain(user, CHAIN_ID3));

        assertQuery("SELECT COUNT(*) = 3 FROM chains");

        user.setId(USER_ID);
        Assert.assertTrue(chainsDao.deleteChain(user, CHAIN_ID1));
        Assert.assertTrue(chainsDao.deleteChain(user, CHAIN_ID2));
        Assert.assertTrue(chainsDao.deleteChain(user, CHAIN_ID3));

        assertQuery("SELECT COUNT(*) = 0 FROM chains");

        Assert.assertFalse(chainsDao.deleteChain(user, CHAIN_ID1));
        Assert.assertFalse(chainsDao.deleteChain(user, CHAIN_ID2));
        Assert.assertFalse(chainsDao.deleteChain(user, CHAIN_ID3));

        assertQuery("SELECT COUNT(*) = 0 FROM chains");
    }

    /**
     * See {@link ChainsDao#deleteChain(User, int)}.
    **/
    @Test
    public void testDeleteChain() {
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

        final int maxId = 20;

        for (int id = 1; id <= maxId; id++) {
            chainsUtils.insert(id, id % 2 == 0 ? USER_ID1 : USER_ID2);
        }

        assertQuery("SELECT COUNT(*) = ? FROM chains", maxId);

        int entries = maxId;
        for (int id = 1; id <= maxId; id++) {
            Assert.assertFalse(chainsDao.deleteChain(user3, id));

            if (id % 2 == 0) {
                Assert.assertFalse(chainsDao.deleteChain(user2, id));
            } else {
                Assert.assertFalse(chainsDao.deleteChain(user1, id));
            }

            assertQuery("SELECT COUNT(*) = ? FROM chains", entries);

            if (id % 2 == 0) {
                Assert.assertTrue(chainsDao.deleteChain(user1, id));
            } else {
                Assert.assertTrue(chainsDao.deleteChain(user2, id));
            }

            entries--;
            assertQuery("SELECT COUNT(*) = ? FROM chains", entries);
        }
    }

    /**
     * See {@link ChainsDao#checkChainBindingExists(User, int, int)}.
    **/
    @Test
    public void testInvalidArgumentsCheckChainBindingExists() {
        final int USER_ID = 12345;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID1 = 1234;
        final int SPACE_ID2 = 2345;

        final int CHAIN_ID1 = 123;
        final int CHAIN_ID2 = 234;

        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID2));

        assertThrows("user is null", () -> chainsDao.checkChainBindingExists(null, CHAIN_ID1, SPACE_ID1));
        assertThrows("user is null", () -> chainsDao.checkChainBindingExists(null, CHAIN_ID2, SPACE_ID1));
        assertThrows("user is null", () -> chainsDao.checkChainBindingExists(null, CHAIN_ID1, SPACE_ID2));
        assertThrows("user is null", () -> chainsDao.checkChainBindingExists(null, CHAIN_ID2, SPACE_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID1));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID1));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID2));
        user.setId(-123);
        assertThrows("user.id <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID2));

        user.setId(USER_ID);
        assertThrows("id <= 0", () -> chainsDao.checkChainBindingExists(user, 0, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.checkChainBindingExists(user, -1, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.checkChainBindingExists(user, -10, SPACE_ID2));
        assertThrows("id <= 0", () -> chainsDao.checkChainBindingExists(user, -100, SPACE_ID2));

        assertThrows("spaceId <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID1, 0));
        assertThrows("spaceId <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID2, -1));
        assertThrows("spaceId <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID1, -12));
        assertThrows("spaceId <= 0", () -> chainsDao.checkChainBindingExists(user, CHAIN_ID2, -123));

        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user, CHAIN_ID2, SPACE_ID2));
    }

    /**
     * See {@link ChainsDao#checkChainBindingExists(User, int, int)}.
    **/
    @Test
    public void testCheckChainBindingExists() {
        final int USER_ID1 = 12345;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 23456;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        // No insertion

        final int SPACE_ID1 = 1234;
        final int SPACE_ID2 = 2345;

        final int CHAIN_ID1 = 123;
        final int CHAIN_ID2 = 234;

        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));

        chainsUtils.insert(CHAIN_ID1, USER_ID1);
        chainsUtils.insert(CHAIN_ID2, USER_ID1);

        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));

        final Space space1 = spacesUtils.generateInstanceWithUniqueName(0);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final Space space2 = spacesUtils.generateInstanceWithUniqueName(0);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));

        chainsUtils.insertBinding(CHAIN_ID1, SPACE_ID1, USER_ID1);

        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));

        chainsUtils.insertBinding(CHAIN_ID1, SPACE_ID2, USER_ID1);
        chainsUtils.insertBinding(CHAIN_ID2, SPACE_ID1, USER_ID1);

        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));

        chainsUtils.deleteBinding(CHAIN_ID1, SPACE_ID1);
        chainsUtils.deleteBinding(CHAIN_ID1, SPACE_ID2);
        chainsUtils.insertBinding(CHAIN_ID2, SPACE_ID2, USER_ID1);

        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID1));
        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user1, CHAIN_ID1, SPACE_ID2));
        Assert.assertTrue(chainsDao.checkChainBindingExists(user1, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.checkChainBindingExists(user2, CHAIN_ID2, SPACE_ID2));
    }

    /**
     * See {@link ChainsDao#createChainBinding(User, int, int)}.
    **/
    @Test
    public void testInvalidArgumentsCreateChainBinding() {
        final int USER_ID = 12345;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID1 = 1234;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID);
        final int SPACE_ID2 = 2345;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID);

        final int CHAIN_ID1 = 123;
        chainsUtils.insert(CHAIN_ID1, USER_ID);
        final int CHAIN_ID2 = 234;
        chainsUtils.insert(CHAIN_ID2, USER_ID);

        assertThrows("user is null", () -> chainsDao.createChainBinding(null, CHAIN_ID1, SPACE_ID1));
        assertThrows("user is null", () -> chainsDao.createChainBinding(null, CHAIN_ID2, SPACE_ID1));
        assertThrows("user is null", () -> chainsDao.createChainBinding(null, CHAIN_ID1, SPACE_ID2));
        assertThrows("user is null", () -> chainsDao.createChainBinding(null, CHAIN_ID2, SPACE_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID1));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID1));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID2));
        user.setId(-123);
        assertThrows("user.id <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID2));

        user.setId(USER_ID);
        assertThrows("id <= 0", () -> chainsDao.createChainBinding(user, 0, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.createChainBinding(user, -1, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.createChainBinding(user, -10, SPACE_ID2));
        assertThrows("id <= 0", () -> chainsDao.createChainBinding(user, -100, SPACE_ID2));

        assertThrows("spaceId <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID1, 0));
        assertThrows("spaceId <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID2, -1));
        assertThrows("spaceId <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID1, -12));
        assertThrows("spaceId <= 0", () -> chainsDao.createChainBinding(user, CHAIN_ID2, -123));

        assertQuery("SELECT COUNT(*) = 0 FROM chain_bindings");

        Assert.assertTrue(chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID1));
        Assert.assertFalse(chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID1));
        Assert.assertTrue(chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID1));
        Assert.assertFalse(chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID1));
        Assert.assertTrue(chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID2));
        Assert.assertFalse(chainsDao.createChainBinding(user, CHAIN_ID1, SPACE_ID2));
        Assert.assertTrue(chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID2));
        Assert.assertFalse(chainsDao.createChainBinding(user, CHAIN_ID2, SPACE_ID2));

        assertQuery("SELECT COUNT(*) = 4 FROM chain_bindings");
    }

    /**
     * See {@link ChainsDao#createChainBinding(User, int, int)}.
    **/
    @Test
    public void testConstraintsCreateChainBinding() {
        final int USER_ID1 = 12345;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 23456;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        // No insertion

        final int SPACE_ID1 = 1234;
        final Space space1 = spacesUtils.generateInstance();
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 2345;
        // No insertion

        final int CHAIN_ID1 = 123;
        chainsUtils.insert(CHAIN_ID1, USER_ID1);

        final int CHAIN_ID2 = 234;
        // No insertion

        assertThrows("user doesn't exist", () -> chainsDao.createChainBinding(user2, CHAIN_ID1, SPACE_ID1), DataAccessException.class);
        assertThrows("chain doesn't exist", () -> chainsDao.createChainBinding(user1, CHAIN_ID2, SPACE_ID1), DataAccessException.class);
        assertThrows("space doesn't exist", () -> chainsDao.createChainBinding(user1, CHAIN_ID1, SPACE_ID2), DataAccessException.class);

        assertQuery("SELECT COUNT(*) = 0 FROM chain_bindings");

        Assert.assertTrue(chainsDao.createChainBinding(user1, CHAIN_ID1, SPACE_ID1));

        assertQuery("SELECT COUNT(*) = 1 FROM chain_bindings");

        Assert.assertFalse(chainsDao.createChainBinding(user1, CHAIN_ID1, SPACE_ID1));

        assertQuery("SELECT COUNT(*) = 1 FROM chain_bindings");
    }

    /**
     * See {@link ChainsDao#createChainBinding(User, int, int)}.
    **/
    @Test
    public void testCreateChainBinding() {
        final int USER_ID1 = 12345;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 23456;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 34567;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        usersUtils.insert(user3);

        final int SPACE_ID1 = 1234;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 2345;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 3456;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID2);

        final int SPACE_ID4 = 4567;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int CHAIN_ID1 = 123;
        chainsUtils.insert(CHAIN_ID1, USER_ID1);
        final int CHAIN_ID2 = 234;
        chainsUtils.insert(CHAIN_ID2, USER_ID1);
        final int CHAIN_ID3 = 345;
        chainsUtils.insert(CHAIN_ID3, USER_ID1);

        final int CHAIN_ID4 = 456;
        chainsUtils.insert(CHAIN_ID4, USER_ID2);
        final int CHAIN_ID5 = 567;
        chainsUtils.insert(CHAIN_ID5, USER_ID2);
        final int CHAIN_ID6 = 678;
        chainsUtils.insert(CHAIN_ID6, USER_ID2);

        final Map<Integer, User> usersMap = new HashMap<Integer, User>() {{
            put(USER_ID1, user1);
            put(USER_ID2, user2);
        }};

        final List<Integer[]> all = new ArrayList<Integer[]>() {{
            add(new Integer[] {USER_ID1, CHAIN_ID1, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID2, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID3, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID1, SPACE_ID2});
            add(new Integer[] {USER_ID1, CHAIN_ID2, SPACE_ID2});
            add(new Integer[] {USER_ID1, CHAIN_ID3, SPACE_ID2});
            add(new Integer[] {USER_ID2, CHAIN_ID4, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID5, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID6, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID4, SPACE_ID4});
            add(new Integer[] {USER_ID2, CHAIN_ID5, SPACE_ID4});
            add(new Integer[] {USER_ID2, CHAIN_ID6, SPACE_ID4});
        }};

        final Set<Integer[]> present = new HashSet<>();
        final Set<Integer[]> absent = new HashSet<>(all);

        Collections.shuffle(all);
        int entries = 0;

        for (Integer[] a : all) {
            assertQuery("SELECT COUNT(*) = ? FROM chain_bindings", entries);

            Assert.assertTrue(chainsDao.createChainBinding(usersMap.get(a[0]), a[1], a[2]));

            entries++;

            assertQuery("SELECT COUNT(*) = ? FROM chain_bindings", entries);

            // Already bound - nothing happens
            Assert.assertFalse(chainsDao.createChainBinding(usersMap.get(a[0]), a[1], a[2]));

            assertQuery("SELECT COUNT(*) = ? FROM chain_bindings", entries);

            present.add(a);
            absent.remove(a);

            for (Integer[] data : absent) {
                Assert.assertFalse(chainsUtils.existsBinding(data[1], data[2], data[0]));
                Assert.assertFalse(chainsUtils.existsBinding(data[1], data[2], USER_ID3));
            }
            for (Integer[] data : present) {
                Assert.assertTrue(chainsUtils.existsBinding(data[1], data[2], data[0]));
                Assert.assertFalse(chainsUtils.existsBinding(data[1], data[2], USER_ID3));
            }
        }
    }

    /**
     * See {@link ChainsDao#deleteChainBinding(User, int, int)}.
    **/
    @Test
    public void testInvalidArgumentsDeleteChainBinding() {
        final int USER_ID = 12345;
        final User user = usersUtils.generateInstance();
        user.setId(USER_ID);
        usersUtils.insert(user);

        final int SPACE_ID1 = 1234;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID);

        final int SPACE_ID2 = 2345;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID);

        final int CHAIN_ID1 = 123;
        final int CHAIN_ID2 = 234;

        chainsUtils.insert(CHAIN_ID1, USER_ID);
        chainsUtils.insert(CHAIN_ID2, USER_ID);

        chainsUtils.insertBinding(CHAIN_ID1, SPACE_ID1, USER_ID);
        chainsUtils.insertBinding(CHAIN_ID2, SPACE_ID2, USER_ID);

        Assert.assertTrue(chainsUtils.exists(CHAIN_ID1, USER_ID));
        Assert.assertTrue(chainsUtils.existsBinding(CHAIN_ID1, SPACE_ID1, USER_ID));
        Assert.assertTrue(chainsUtils.exists(CHAIN_ID2, USER_ID));
        Assert.assertTrue(chainsUtils.existsBinding(CHAIN_ID2, SPACE_ID2, USER_ID));

        assertThrows("user is null", () -> chainsDao.deleteChainBinding(null, CHAIN_ID1, SPACE_ID1));
        assertThrows("user is null", () -> chainsDao.deleteChainBinding(null, CHAIN_ID2, SPACE_ID2));

        user.setId(0);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, SPACE_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, SPACE_ID2));
        user.setId(-1);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, SPACE_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, SPACE_ID2));
        user.setId(-10);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, SPACE_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, SPACE_ID2));
        user.setId(-100);
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, SPACE_ID1));
        assertThrows("user.id <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, SPACE_ID2));
        user.setId(USER_ID);

        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, 0, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, 0, SPACE_ID2));
        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, -1, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, -1, SPACE_ID2));
        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, -10, SPACE_ID1));
        assertThrows("id <= 0", () -> chainsDao.deleteChainBinding(user, -10, SPACE_ID2));

        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, 0));
        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, 0));
        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, -1));
        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, -1));
        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID1, -10));
        assertThrows("spaceId <= 0", () -> chainsDao.deleteChainBinding(user, CHAIN_ID2, -10));

        Assert.assertTrue(chainsUtils.exists(CHAIN_ID1, USER_ID));
        Assert.assertTrue(chainsUtils.existsBinding(CHAIN_ID1, SPACE_ID1, USER_ID));
        Assert.assertTrue(chainsUtils.exists(CHAIN_ID2, USER_ID));
        Assert.assertTrue(chainsUtils.existsBinding(CHAIN_ID2, SPACE_ID2, USER_ID));

        Assert.assertTrue(chainsDao.deleteChainBinding(user, CHAIN_ID1, SPACE_ID1));
        Assert.assertTrue(chainsDao.deleteChainBinding(user, CHAIN_ID2, SPACE_ID2));

        Assert.assertTrue(chainsUtils.exists(CHAIN_ID1, USER_ID));
        Assert.assertFalse(chainsUtils.existsBinding(CHAIN_ID1, SPACE_ID1, USER_ID));
        Assert.assertTrue(chainsUtils.exists(CHAIN_ID2, USER_ID));
        Assert.assertFalse(chainsUtils.existsBinding(CHAIN_ID2, SPACE_ID2, USER_ID));
    }

    /**
     * See {@link ChainsDao#deleteChainBinding(User, int, int)}.
    **/
    @Test
    public void testDeleteChainBinding() {
        final int USER_ID1 = 12345;
        final User user1 = usersUtils.generateInstanceWithUniqueName();
        user1.setId(USER_ID1);
        usersUtils.insert(user1);

        final int USER_ID2 = 23456;
        final User user2 = usersUtils.generateInstanceWithUniqueName();
        user2.setId(USER_ID2);
        usersUtils.insert(user2);

        final int USER_ID3 = 34567;
        final User user3 = usersUtils.generateInstanceWithUniqueName();
        user3.setId(USER_ID3);
        usersUtils.insert(user3);

        final int SPACE_ID1 = 1234;
        final Space space1 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space1.setId(SPACE_ID1);
        spacesUtils.insert(space1, USER_ID1);

        final int SPACE_ID2 = 2345;
        final Space space2 = spacesUtils.generateInstanceWithUniqueName(USER_ID1);
        space2.setId(SPACE_ID2);
        spacesUtils.insert(space2, USER_ID1);

        final int SPACE_ID3 = 3456;
        final Space space3 = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
        space3.setId(SPACE_ID3);
        spacesUtils.insert(space3, USER_ID2);

        final int SPACE_ID4 = 4567;
        final Space space4 = spacesUtils.generateInstanceWithUniqueName(USER_ID2);
        space4.setId(SPACE_ID4);
        spacesUtils.insert(space4, USER_ID2);

        final int CHAIN_ID1 = 123;
        chainsUtils.insert(CHAIN_ID1, USER_ID1);
        final int CHAIN_ID2 = 234;
        chainsUtils.insert(CHAIN_ID2, USER_ID1);
        final int CHAIN_ID3 = 345;
        chainsUtils.insert(CHAIN_ID3, USER_ID1);

        final int CHAIN_ID4 = 456;
        chainsUtils.insert(CHAIN_ID4, USER_ID2);
        final int CHAIN_ID5 = 567;
        chainsUtils.insert(CHAIN_ID5, USER_ID2);
        final int CHAIN_ID6 = 678;
        chainsUtils.insert(CHAIN_ID6, USER_ID2);

        final Map<Integer, User> usersMap = new HashMap<Integer, User>() {{
            put(USER_ID1, user1);
            put(USER_ID2, user2);
        }};

        final List<Integer[]> all = new ArrayList<Integer[]>() {{
            add(new Integer[] {USER_ID1, CHAIN_ID1, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID2, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID3, SPACE_ID1});
            add(new Integer[] {USER_ID1, CHAIN_ID1, SPACE_ID2});
            add(new Integer[] {USER_ID1, CHAIN_ID2, SPACE_ID2});
            add(new Integer[] {USER_ID1, CHAIN_ID3, SPACE_ID2});
            add(new Integer[] {USER_ID2, CHAIN_ID4, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID5, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID6, SPACE_ID3});
            add(new Integer[] {USER_ID2, CHAIN_ID4, SPACE_ID4});
            add(new Integer[] {USER_ID2, CHAIN_ID5, SPACE_ID4});
            add(new Integer[] {USER_ID2, CHAIN_ID6, SPACE_ID4});
        }};

        final Set<Integer[]> present = new HashSet<>();
        final Set<Integer[]> absent = new HashSet<>(all);

        final int count = 40;

        for (int i = 0; i < count / 2; i++) {
            final Integer[] data = chainsUtils.any(all);

            if (!present.contains(data)) {
                chainsUtils.insertBinding(data[1], data[2], data[0]);
                absent.remove(data);
                present.add(data);
            }
        }

        for (int i = 0; i < count; i++) {
            final Integer[] data = chainsUtils.any(all);

            if (chainsUtils.existsBinding(data[1], data[2], data[0])) {
                Assert.assertTrue(chainsDao.deleteChainBinding(usersMap.get(data[0]), data[1], data[2]));
                present.remove(data);
                absent.add(data);
            } else {
                if (random.nextBoolean()) {
                    Assert.assertFalse(chainsDao.deleteChainBinding(usersMap.get(data[0]), data[1], data[2]));
                } else {
                    chainsUtils.insertBinding(data[1], data[2], data[0]);
                    absent.remove(data);
                    present.add(data);
                }
            }

            for (Integer[] a : absent) {
                Assert.assertFalse(chainsUtils.existsBinding(a[1], a[2], a[0]));
            }
            for (Integer[] p : present) {
                Assert.assertTrue(chainsUtils.existsBinding(p[1], p[2], p[0]));
            }
            assertQuery("SELECT COUNT(*) = ? FROM chain_bindings", present.size());
        }
    }
}
