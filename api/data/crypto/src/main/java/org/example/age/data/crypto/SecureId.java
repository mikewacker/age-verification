package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;

/** 256 random bits, generated using a cryptographically strong random number generator. Can also be used as a key. */
public final class SecureId extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

    /** Creates an ID from a copy of the bytes. */
    public static SecureId ofBytes(byte[] rawId) {
        return new SecureId(rawId, true);
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
        byte[] localId = HmacUtils.createHmac(uncopiedBytes(), key.uncopiedBytes());
        return ofUncopiedBytes(localId);
    }

    /** Creates an ID from the bytes. */
    @JsonCreator
    static SecureId ofUncopiedBytes(byte[] id) {
        return new SecureId(id, false);
    }

    private SecureId(byte[] id, boolean copy) {
        super(id, copy, EXPECTED_LENGTH);
    }

    private SecureId() {
        super(EXPECTED_LENGTH);
    }
}
