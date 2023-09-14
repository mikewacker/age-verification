package org.example.age.internal;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Type that is backed by immutable bytes, which can be converted to and from URL-friendly base64 text.
 *
 * <p>To serialize and deserialize a concrete implementation to and from JSON,
 * an implementation can use {@link ToStringSerializer} and {@link StaticFromStringDeserializer}.</p>
 *
 * <p>If the bytes have an expected length, {@link #expectedLength()} can be overridden to specify that length.</p>
 *
 * <p>Concrete implementations should have a flat hierarchy; {@link #equals(Object)} uses {@link Object#getClass()}.</p>
 */
public abstract class ImmutableBytes {

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    private static final SecureRandom random = new SecureRandom();

    protected final byte[] bytes;

    /** Creates immutable bytes from a copy of the raw bytes. */
    protected ImmutableBytes(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
        checkLength();
    }

    /** Creates immutable bytes from URL-friendly base64 text. */
    protected ImmutableBytes(String value) {
        this.bytes = decoder.decode(value);
        checkLength();
    }

    /** Generates immutable bytes using a cryptographically strong random number generator. */
    protected ImmutableBytes() {
        if (expectedLength() == 0) {
            throw new IllegalStateException("expected length must be set");
        }

        bytes = new byte[expectedLength()];
        random.nextBytes(bytes);
    }

    /** Gets a copy of the raw bytes. */
    public final byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /** Converts the raw bytes to URL-friendly base64 text. */
    @Override
    public final String toString() {
        return encoder.encodeToString(bytes);
    }

    @Override
    @SuppressWarnings("EqualsGetClass")
    public final boolean equals(Object o) {
        if ((o == null) || !getClass().equals(o.getClass())) {
            return false;
        }

        ImmutableBytes other = (ImmutableBytes) o;
        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /** Gets the expected length in bytes, or 0 if any length is acceptable. Defaults to 0. */
    protected int expectedLength() {
        return 0;
    }

    /** Checks the length in bytes. */
    private void checkLength() {
        if (expectedLength() == 0) {
            return;
        }

        if (bytes.length != expectedLength()) {
            String message = String.format("expected %d bits", 8 * expectedLength());
            throw new IllegalArgumentException(message);
        }
    }
}
