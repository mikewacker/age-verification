package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;

/** AES-256 key. */
public final class Aes256Key extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

    /** Creates a key from a copy of the bytes. */
    public static Aes256Key ofBytes(byte[] key) {
        return new Aes256Key(key, true);
    }

    /** Generates a new key. */
    public static Aes256Key generate() {
        return new Aes256Key();
    }

    /** Creates a key from the bytes. */
    @JsonCreator
    static Aes256Key ofUncopiedBytes(byte[] key) {
        return new Aes256Key(key, false);
    }

    private Aes256Key(byte[] key, boolean copy) {
        super(key, copy, EXPECTED_LENGTH);
    }

    private Aes256Key() {
        super(EXPECTED_LENGTH);
    }
}
