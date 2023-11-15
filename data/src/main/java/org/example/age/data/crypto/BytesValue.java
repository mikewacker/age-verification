package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.data.internal.ImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** Bytes as a value. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = BytesValue.Deserializer.class)
public class BytesValue extends ImmutableBytes {

    /** Creates a value from a copy of the raw bytes. */
    public static BytesValue ofBytes(byte[] rawValue) {
        return new BytesValue(rawValue);
    }

    /** Deserializes a value from URL-friendly base64 text. */
    public static BytesValue fromString(String rawValue) {
        return new BytesValue(rawValue);
    }

    /**
     * Creates an empty value.
     *
     * <p>The raw bytes are a single zero-filled byte; empty bytes cannot be deserialized properly.</p>
     */
    public static BytesValue empty() {
        return new BytesValue(new byte[1], false);
    }

    /** Creates a value the raw bytes. */
    static BytesValue ofUncopiedBytes(byte[] rawValue) {
        return new BytesValue(rawValue, false);
    }

    /** Gets the raw bytes. */
    byte[] uncopiedBytes() {
        return bytes;
    }

    private BytesValue(byte[] rawValue) {
        super(rawValue);
    }

    private BytesValue(String rawValue) {
        super(rawValue);
    }

    private BytesValue(byte[] rawValue, boolean copy) {
        super(rawValue, copy);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<BytesValue> {

        public Deserializer() {
            super(BytesValue.class, BytesValue::fromString);
        }
    }
}
