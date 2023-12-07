package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Immutable wrapper for a fixed-length {@code byte[]} value,
 * which is randomly generated using a cryptographically strong random number generator.
 *
 * <p>A concrete implementation should...</p>
 * <ul>
 *     <li>be a {@code final} class</code>; {@link #equals(Object)} uses {@link #getClass()}.</li>
 *     <li>provide a public static factory method, {@code ofBytes(byte[])}, which copies the bytes.</li>
 *     <li>provide a public static factory method, {@code generate()}, which randomly generates the bytes.</li>
 *     <li>provide a package-private static factory method, {@code ofUncopiedBytes(byte[])},
 *         which is annotated with <code>@{@link JsonCreator}</code>.</li>
 * </ul>
 */
abstract class SecureRandomImmutableBytes extends ImmutableBytes {

    /** Creates a wrapper from the bytes, which may be copied, checking that the bytes have the expected length. */
    protected SecureRandomImmutableBytes(byte[] value, boolean copy, int expectedLength) {
        super(checkLength(value, expectedLength), copy);
    }

    /** Creates a wrapper by randomly generating the specified number of bytes. */
    protected SecureRandomImmutableBytes(int length) {
        super(SecureRandomUtils.generateBytes(length), false);
    }

    /** Checks that the bytes have the expected length. */
    private static byte[] checkLength(byte[] value, int expectedLength) {
        if (value.length != expectedLength) {
            String message = String.format("expected %d bits", 8 * expectedLength);
            throw new IllegalArgumentException(message);
        }

        return value;
    }
}
