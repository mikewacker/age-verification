package org.example.age.data.crypto.internal;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Utilities for HMACs. */
public final class HmacUtils {

    /** Creates an HMAC from the message and the key. */
    public static byte[] createHmac(byte[] message, byte[] key) {
        try {
            Mac hmacFactory = Macs.createHmacFactory(key);
            return hmacFactory.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("HMAC creation failed", e);
        }
    }

    // static class
    private HmacUtils() {}

    /** Creates {@link Mac}'s. */
    private static final class Macs {

        private static final String ALGORITHM = "HmacSHA256";

        /** Creates an HMAC factory. */
        public static Mac createHmacFactory(byte[] rawKey) throws InvalidKeyException {
            Mac hmacFactory = newHmacFactory();
            Key key = createKey(rawKey);
            hmacFactory.init(key);
            return hmacFactory;
        }

        /** Creates an uninitialized HMAC factory. */
        private static Mac newHmacFactory() {
            try {
                return Mac.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("unexpected error", e);
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
