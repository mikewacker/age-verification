package org.example.age.data.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilities for HMACs.
 *
 * <p>Keys will be generated internally.</p>
 */
public final class HmacUtils {

    /** Creates an HMAC from the message and the key. */
    public static byte[] createHmac(byte[] message, byte[] key) {
        Mac hmacFactory = Macs.createHmacFactory(key);
        return hmacFactory.doFinal(message);
    }

    // static class
    private HmacUtils() {}

    /** Creates {@link Mac}'s. */
    private static final class Macs {

        private static final String ALGORITHM = "HmacSHA256";

        /** Creates an HMAC factory. */
        public static Mac createHmacFactory(byte[] rawKey) {
            Mac hmacFactory = newHmacFactory();
            Key key = createKey(rawKey);
            try {
                hmacFactory.init(key);
                return hmacFactory;
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }

        /** Creates an uninitialized HMAC factory. */
        private static Mac newHmacFactory() {
            try {
                return Mac.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        /** Creates a key from the raw bytes. */
        private static Key createKey(byte[] rawKey) {
            return new SecretKeySpec(rawKey, ALGORITHM);
        }

        // static class
        private Macs() {}
    }
}
