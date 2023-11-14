package org.example.age.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.data.crypto.HmacUtils;
import org.example.age.data.internal.SecureRandomImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** 256 random bits, generated via a secure random number generator. Can also be used as a key. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = SecureId.Deserializer.class)
public final class SecureId extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

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
        byte[] localBytes = HmacUtils.createHmac(bytes, key.bytes);
        return new SecureId(localBytes);
    }

    private SecureId() {
        super(EXPECTED_LENGTH);
    }

    private SecureId(byte[] bytes) {
        super(bytes, EXPECTED_LENGTH);
    }

    private SecureId(String value) {
        super(value, EXPECTED_LENGTH);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<SecureId> {

        public Deserializer() {
            super(SecureId.class, SecureId::fromString);
        }
    }
}
