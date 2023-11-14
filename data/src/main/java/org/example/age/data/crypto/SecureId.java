package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** 256 random bits, generated using a cryptographically strong random number generator. Can also be used as a key. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = SecureId.Deserializer.class)
public final class SecureId extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

    /** Creates an ID from a copy of the raw bytes. */
    public static SecureId ofBytes(byte[] rawId) {
        return new SecureId(rawId);
    }

    /** Deserializes an ID from URL-friendly base64 text. */
    public static SecureId fromString(String rawId) {
        return new SecureId(rawId);
    }

    /** Generates a new ID. */
    public static SecureId generate() {
        return new SecureId();
    }

    /**
     * Produces a new ID based on a key.
     *
     * <p>It is impossible to figure out the original ID from the new ID,
     * and it is impossible to figure out the new ID from the original ID (without knowledge of the key).</p>
     */
    public SecureId localize(SecureId key) {
        byte[] localRawId = HmacUtils.createHmac(bytes, key.bytes);
        return ofUncopiedBytes(localRawId);
    }

    /** Creates an ID from the raw bytes. */
    private static SecureId ofUncopiedBytes(byte[] rawId) {
        return new SecureId(rawId, false);
    }

    private SecureId(byte[] rawId) {
        super(rawId, EXPECTED_LENGTH);
    }

    private SecureId(String rawId) {
        super(rawId, EXPECTED_LENGTH);
    }

    private SecureId() {
        super(EXPECTED_LENGTH);
    }

    private SecureId(byte[] rawId, boolean copy) {
        super(rawId, copy, EXPECTED_LENGTH);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<SecureId> {

        public Deserializer() {
            super(SecureId.class, SecureId::fromString);
        }
    }
}
