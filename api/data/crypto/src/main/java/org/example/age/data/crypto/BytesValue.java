package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;

/** Bytes as a value. */
public final class BytesValue extends ImmutableBytes {

    /** Creates a value from a copy of the bytes. */
    public static BytesValue ofBytes(byte[] value) {
        return new BytesValue(value, true);
    }

    /** Creates an empty value. */
    public static BytesValue empty() {
        return new BytesValue(new byte[0], false);
    }

    /** Creates a value from the bytes. */
    @JsonCreator
    static BytesValue ofUncopiedBytes(byte[] rawValue) {
        return new BytesValue(rawValue, false);
    }

    private BytesValue(byte[] value, boolean copy) {
        super(value, copy);
    }
}
