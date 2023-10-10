package org.example.age.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.example.age.data.internal.ImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** 256 random bits, generated via a secure random number generator. Can also be used as a key. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = SecureId.Deserializer.class)
public final class SecureId extends ImmutableBytes {

    /** Generates a new ID. */
    public static SecureId generate() {
        return new SecureId();
    }

    /** Creates an ID from a copy of the raw bytes. */
    public static SecureId ofBytes(byte[] bytes) {
        return new SecureId(bytes);
    }

    /** Creates an ID from URL-friendly base64 text. */
    public static SecureId fromString(String value) {
        return new SecureId(value);
    }

    /**
     * Produces a new ID based on a key.
     *
     * <p>It is impossible to figure out the original ID from the new ID,
     * and it is impossible to figure out the new ID from the original ID (without knowledge of the key).</p>
     */
    public SecureId localize(SecureId key) {
        Mac hmacGenerator = Macs.createHmacGenerator(key.bytes);
        byte[] localBytes = hmacGenerator.doFinal(bytes);
        return new SecureId(localBytes);
    }

    @Override
    protected int expectedLength() {
        return 32;
    }

    private SecureId() {}

    private SecureId(byte[] bytes) {
        super(bytes);
    }

    private SecureId(String value) {
        super(value);
    }

    /** Creates {@link Mac}'s. */
    private static final class Macs {

        private static final String ALGORITHM = "HmacSHA256";

        /** Creates an HMAC generator. */
        public static Mac createHmacGenerator(byte[] rawKey) {
            try {
                Mac hmacGenerator = Mac.getInstance(ALGORITHM);
                Key hmacKey = new SecretKeySpec(rawKey, ALGORITHM);
                hmacGenerator.init(hmacKey);
                return hmacGenerator;
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("unexpected error", e);
            }
        }

        // static class
        private Macs() {}
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<SecureId> {

        public Deserializer() {
            super(SecureId.class, SecureId::fromString);
        }
    }
}
