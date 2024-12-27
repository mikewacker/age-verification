package org.example.age.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 256-bit ID that is securely generated. Encoded in a URL-friendly base64 format. */
public final class SecureId extends ImmutableBytes {

    private static final int LENGTH_BYTES = 32; // 256 bits
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final SecureRandom secureRandom = new SecureRandom();

    /** Generates an ID. */
    public static SecureId generate() {
        byte[] bytes = new byte[LENGTH_BYTES];
        secureRandom.nextBytes(bytes);
        return new SecureId(bytes);
    }

    /** Converts a string to an ID for the purpose of JSON deserialization. */
    @JsonCreator
    public static SecureId fromString(String text) {
        byte[] bytes = bytesFromString(text);
        if (bytes.length != LENGTH_BYTES) {
            throw new IllegalArgumentException("SecureId must have 256 bits");
        }

        return new SecureId(bytes);
    }

    /**
     * Localizes this ID using a key.
     * <p>
     * It is impossible to...
     * <ul>
     *     <li>figure out the original ID from the localized ID.
     *     <li>figure out the localized ID from the original ID without knowledge of the key.
     * </ul>
     */
    public SecureId localize(SecureId key) {
        Mac hmacFactory = createHmacFactory(key.bytes);
        byte[] localizedBytes = hmacFactory.doFinal(bytes);
        return new SecureId(localizedBytes);
    }

    /** Creates an HMAC factory. */
    private static Mac createHmacFactory(byte[] rawKey) {
        try {
            Mac hmacFactory = Mac.getInstance(HMAC_ALGORITHM);
            Key key = new SecretKeySpec(rawKey, HMAC_ALGORITHM);
            hmacFactory.init(key);
            return hmacFactory;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("unexpected error", e);
        }
    }

    private SecureId(byte[] bytes) {
        super(bytes);
    }
}
