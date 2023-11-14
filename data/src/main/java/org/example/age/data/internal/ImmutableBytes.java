package org.example.age.data.internal;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Arrays;
import java.util.Base64;

/**
 * Type that is backed by immutable bytes. The type can also be serialized as URL-friendly base64 text.
 *
 * <p>To serialize and deserialize a concrete implementation to and from JSON,
 * an implementation can use {@link ToStringSerializer} and {@link StaticFromStringDeserializer}.</p>
 *
 * <p>Concrete implementations should be {@code final}; {@link #equals(Object)} uses {@link Object#getClass()}.</p>
 */
public abstract class ImmutableBytes {

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    protected final byte[] bytes;

    /** Creates immutable bytes from a copy of the bytes. */
    protected ImmutableBytes(byte[] bytes) {
        this(bytes, true);
    }

    /** Deserializes immutable bytes from URL-friendly base64 text. */
    protected ImmutableBytes(String text) {
        this(decoder.decode(text), false);
    }

    /** Creates immutable bytes from the bytes, which may be copied. */
    protected ImmutableBytes(byte[] bytes, boolean copy) {
        this.bytes = copy ? Arrays.copyOf(bytes, bytes.length) : bytes;
    }

    /** Gets a copy of the raw bytes. */
    public final byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /** Serializes the raw bytes to URL-friendly base64 text. */
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
}
