package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** Ephemeral AES-256 key used for encrypting authentication data. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = AuthKey.Deserializer.class)
public final class AuthKey extends SecureRandomImmutableBytes {

    private static final int EXPECTED_LENGTH = 32;

    /** Generates a new key. */
    public static AuthKey generate() {
        return new AuthKey();
    }

    /** Creates a key from a copy of the raw bytes. */
    public static AuthKey ofBytes(byte[] bytes) {
        return new AuthKey(bytes);
    }

    /** Creates a key from URL-friendly base64 text. */
    public static AuthKey fromString(String value) {
        return new AuthKey(value);
    }

    private AuthKey() {
        super(EXPECTED_LENGTH);
    }

    private AuthKey(byte[] bytes) {
        super(bytes, EXPECTED_LENGTH);
    }

    private AuthKey(String value) {
        super(value, EXPECTED_LENGTH);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<AuthKey> {

        public Deserializer() {
            super(AuthKey.class, AuthKey::fromString);
        }
    }
}
