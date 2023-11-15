package org.example.age.data.crypto.internal;

import org.example.age.data.internal.ImmutableBytes;

/**
 * Type that is backed by immutable bytes of a fixed length; the bytes are generated using
 * a cryptographically strong random number generator. The type can also be serialized as URL-friendly base64 text.
 */
public abstract class SecureRandomImmutableBytes extends ImmutableBytes {

    /** Creates immutable bytes from a copy of the raw bytes, checking the length of the bytes. */
    protected SecureRandomImmutableBytes(byte[] bytes, int expectedLength) {
        super(bytes);
        checkLength(expectedLength);
    }

    /** Deserializes immutable bytes from URL-friendly base64 text, checking the length of the bytes. */
    protected SecureRandomImmutableBytes(String text, int expectedLength) {
        super(text);
        checkLength(expectedLength);
    }

    /**
     * Creates immutable bytes by generating the specified number of raw bytes,
     * using a cryptographically strong random number generator.
     */
    protected SecureRandomImmutableBytes(int length) {
        super(SecureRandomUtils.generateBytes(length), false);
    }

    /**
     * Creates immutable bytes from the raw bytes, which may be copied, checking the length of the bytes.
     *
     * <p>For internal use only.</p>
     */
    protected SecureRandomImmutableBytes(byte[] bytes, boolean copy, int expectedLength) {
        super(bytes, copy);
        checkLength(expectedLength);
    }

    /** Checks that the bytes have the expected length. */
    private void checkLength(int expectedLength) {
        if (bytes.length != expectedLength) {
            String message = String.format("expected %d bits", 8 * expectedLength);
            throw new IllegalArgumentException(message);
        }
    }
}
