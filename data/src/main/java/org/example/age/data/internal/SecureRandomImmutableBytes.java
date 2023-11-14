package org.example.age.data.internal;

import org.example.age.data.crypto.SecureRandomUtils;

/**
 * Type that is backed by immutable bytes of a fixed length; the bytes are generated using
 * a cryptographically strong random number generator. The type can also be serialized as URL-friendly base64 text.
 */
public abstract class SecureRandomImmutableBytes extends ImmutableBytes {

    /** Creates immutable bytes from a copy of the bytes, checking the length of the bytes. */
    protected SecureRandomImmutableBytes(byte[] bytes, int expectedLength) {
        super(bytes);
        checkLength(expectedLength);
    }

    /** Deserializes immutable bytes from URL-friendly base64 text, checking the length of the bytes. */
    protected SecureRandomImmutableBytes(String text, int expectedLength) {
        super(text);
        checkLength(expectedLength);
    }

    /** Generates the specified number of bytes using a cryptographically strong random number generator. */
    protected SecureRandomImmutableBytes(int length) {
        super(SecureRandomUtils.generateBytes(length), false);
    }

    /** Checks that the bytes have the expected length. */
    private void checkLength(int expectedLength) {
        if (bytes.length != expectedLength) {
            String message = String.format("expected %d bits", 8 * expectedLength);
            throw new IllegalArgumentException(message);
        }
    }
}
