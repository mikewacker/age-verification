package org.example.age.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.example.age.internal.ImmutableBytes;
import org.example.age.internal.StaticFromStringDeserializer;

/** Ephemeral AES-256 key used for encrypting authentication data. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = AuthKey.Deserializer.class)
public final class AuthKey extends ImmutableBytes {

    private final SecretKey secretKey;

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

    /** Converts the key to a {@link SecretKey}. */
    public SecretKey toSecretKey() {
        return secretKey;
    }

    @Override
    protected int expectedLength() {
        return 32;
    }

    private AuthKey() {
        secretKey = SecretKeys.createAesKey(bytes);
    }

    private AuthKey(byte[] bytes) {
        super(bytes);
        secretKey = SecretKeys.createAesKey(bytes);
    }

    private AuthKey(String value) {
        super(value);
        secretKey = SecretKeys.createAesKey(bytes);
    }

    /** Creates {@link SecretKey}'s. */
    private static final class SecretKeys {

        /** Creates an AES key. */
        public static SecretKey createAesKey(byte[] rawKey) {
            return new SecretKeySpec(rawKey, "AES");
        }

        // static class
        private SecretKeys() {}
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<AuthKey> {

        public Deserializer() {
            super(AuthKey.class, AuthKey::fromString);
        }
    }
}
