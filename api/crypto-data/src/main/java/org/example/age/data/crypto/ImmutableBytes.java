package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.example.age.api.JsonObjects;

/**
 * Immutable wrapper for a {@code byte[]} value.
 *
 * <p>A concrete implementation should...</p>
 * <ul>
 *     <li>be a {@code final} class</code>; {@link #equals(Object)} uses {@link #getClass()}.</li>
 *     <li>provide a public static factory method, {@code ofBytes(byte[])}, which copies the bytes.</li>
 *     <li>provide a package-private static factory method, {@code ofUncopiedBytes(byte[])},
 *         which is annotated with <code>@{@link JsonCreator}</code>.</li>
 * </ul>
 */
abstract class ImmutableBytes {

    private final byte[] value;

    /** Gets a copy of the underlying bytes. */
    public final byte[] bytes() {
        return Arrays.copyOf(value, value.length);
    }

    @SuppressWarnings("EqualsGetClass")
    @Override
    public final boolean equals(Object o) {
        if ((o == null) || !getClass().equals(o.getClass())) {
            return false;
        }

        ImmutableBytes other = (ImmutableBytes) o;
        return Arrays.equals(value, other.value);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(value);
    }

    /** Converts the value to a URL-friendly base64 encoding. */
    @Override
    public String toString() {
        byte[] rawValue = JsonObjects.serialize(value);
        String json = new String(rawValue, StandardCharsets.UTF_8);
        return json.substring(1, json.length() - 1); // remove quotes
    }

    /** Gets the underlying bytes. */
    @JsonValue
    byte[] uncopiedBytes() {
        return value;
    }

    /** Creates a wrapper from the bytes, which may be copied. */
    protected ImmutableBytes(byte[] value, boolean copy) {
        this.value = copy ? Arrays.copyOf(value, value.length) : value;
    }
}
