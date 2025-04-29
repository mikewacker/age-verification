package org.example.age.api.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Base64;

/**
 * Immutable bytes that are serialized as a JSON string. Encoded in a URL-friendly base64 format.
 * <p>
 * Concrete implementations should be a {@code final} class with a static factory method:
 * <code>@{@link JsonCreator} fromString(String text)</code>.
 */
public abstract class ImmutableBytes {

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    protected byte[] bytes;

    /** Gets a copy of the raw bytes. */
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /** Converts this instance to a string for the purpose of JSON serialization. */
    @JsonValue
    @Override
    public String toString() {
        return encoder.encodeToString(bytes);
    }

    @Override
    public final boolean equals(Object o) {
        return (o instanceof ImmutableBytes other)
                && getClass().equals(other.getClass())
                && Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /** Creates immutable bytes from the raw bytes. */
    protected ImmutableBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /** Decodes bytes from text. */
    protected static byte[] bytesFromString(String text) {
        return decoder.decode(text);
    }
}
