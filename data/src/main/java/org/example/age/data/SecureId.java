package org.example.age.data;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 256 random bits, generated via a secure random number generator. Can also be used as a key. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = SecureId.Deserializer.class)
public final class SecureId {

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    private final byte[] bytes;

    /** Generates a new ID. */
    public static SecureId generate() {
        byte[] bytes = generate256Bits();
        return new SecureId(bytes);
    }

    /** Creates an ID from a copy of the raw bytes. */
    public static SecureId ofBytes(byte[] bytes) {
        checkHas256Bits(bytes);
        return new SecureId(Arrays.copyOf(bytes, bytes.length));
    }

    /** Creates an ID from a URL-friendly base64 string. */
    public static SecureId fromString(String value) {
        byte[] bytes = decoder.decode(value);
        checkHas256Bits(bytes);
        return new SecureId(bytes);
    }

    /** Gets a copy of the raw bytes. */
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Produces a new ID based on a key.
     *
     * <p>It is impossible to figure out the original ID from the new ID,
     * and it is impossible to figure out the new ID from the original ID (without knowledge of the key).</p>
     */
    public SecureId localize(SecureId key) {
        Mac hmacGenerator = Macs.createHmacGenerator(key.getBytes());
        byte[] localBytes = hmacGenerator.doFinal(bytes);
        return new SecureId(localBytes);
    }

    /** Converts the ID to a URL-friendly base64 string. */
    @Override
    public String toString() {
        return encoder.encodeToString(bytes);
    }

    @Override
    public boolean equals(Object o) {
        SecureId other = (o instanceof SecureId) ? (SecureId) o : null;
        if (other == null) {
            return false;
        }

        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /** Randomly generates 256 bits. */
    private static byte[] generate256Bits() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    /** Checks that we have 256 bits (or 32 bytes). */
    private static void checkHas256Bits(byte[] bytes) {
        if (bytes.length != 32) {
            throw new IllegalArgumentException("secure ID must have 256 bits");
        }
    }

    private SecureId(byte[] bytes) {
        this.bytes = bytes;
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
    static final class Deserializer extends FromStringDeserializer<SecureId> {

        public Deserializer() {
            super(SecureId.class);
        }

        @Override
        protected SecureId _deserialize(String value, DeserializationContext context) {
            return SecureId.fromString(value);
        }
    }
}
