package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** AES-256 key. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = Aes256Key.Deserializer.class)
public final class Aes256Key extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

    /** Creates a key from a copy of the raw bytes. */
    public static Aes256Key ofBytes(byte[] rawKey) {
        return new Aes256Key(rawKey);
    }

    /** Deserializes a key from URL-friendly base64 text. */
    public static Aes256Key fromString(String rawKey) {
        return new Aes256Key(rawKey);
    }

    /** Generates a new key. */
    public static Aes256Key generate() {
        return new Aes256Key();
    }

    /** Gets the raw bytes. */
    byte[] uncopiedBytes() {
        return bytes;
    }

    private Aes256Key(byte[] rawKey) {
        super(rawKey, EXPECTED_LENGTH);
    }

    private Aes256Key(String rawKey) {
        super(rawKey, EXPECTED_LENGTH);
    }

    private Aes256Key() {
        super(EXPECTED_LENGTH);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<Aes256Key> {

        public Deserializer() {
            super(Aes256Key.class, Aes256Key::fromString);
        }
    }
}
