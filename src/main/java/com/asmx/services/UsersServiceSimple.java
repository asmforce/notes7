package com.asmx.services;

import com.asmx.Constants;
import com.asmx.Utils;
import com.asmx.data.daos.UsersDao;
import com.asmx.data.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * User: asmforce
 * Timestamp: 06.06.15 20:24.
**/
@Service
public class UsersServiceSimple implements UsersService {
    private static final Logger logger = Logger.getLogger(UsersServiceSimple.class);

    protected static final String HASHING_ALGORITHM = "PBKDF2WithHmacSHA1";
    protected static final int HASHING_ITERATIONS = 512;
    protected static final int HASH_MAX_LENGTH = User.KEY_MAX_LENGTH;

    @Autowired
    private UsersDao usersDao;
    @Value("${users.salt}")
    private String salt;

    @Override
    public User authorize(String name, String password, HttpSession session) {
        User user = authorize(name, password);
        session.setAttribute(Constants.AUTHORIZED_USER, user);
        if (user != null) {
            session.setAttribute(Constants.AUTHORIZED_USER_LOCALE, Utils.getLocale(user));
        }
        return user;
    }

    private User authorize(String name, String password) {
        assert StringUtils.isNotBlank(name);
        assert StringUtils.isNotBlank(password);

        User user = usersDao.getUser(name);
        if (user != null) {
            try {
                String key = getHashedPassword(password);
                if (StringUtils.equals(key, user.getKey())) {
                    logger.debug("User #" + user.getId() + " `" + user.getName() + "` credentials accepted");
                    return user;
                } else {
                    logger.debug("User #" + user.getId() + " `" + user.getName() + "` credentials rejected");
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("Unable to perform an authorization procedure", e);
            }
        }
        return null;
    }

    private String getHashedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Raw key data needs 25% less memory than base64-encoded one
        final int maxRawHashSize = (int) (HASH_MAX_LENGTH * 0.75f);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), HASHING_ITERATIONS, maxRawHashSize);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(HASHING_ALGORITHM);

        SecretKey key = factory.generateSecret(spec);
        byte[] keyBytes = key.getEncoded();

        Base64.Encoder encoder = Base64.getMimeEncoder();
        return encoder.encodeToString(keyBytes);
    }
}
