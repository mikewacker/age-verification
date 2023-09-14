package org.example.age.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.internal.ImmutableBytes;
import org.example.age.internal.StaticFromStringDeserializer;

/**
 * Encrypted data used to assist with authentication.
 *
 * <p>The data could contain something such as an IP address, which is why it is encrypted.</p>
 */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = AuthToken.Deserializer.class)
public final class AuthToken extends ImmutableBytes {

    /** Creates a token by encrypting authentication data. */
    public static AuthToken encrypt(byte[] data, AuthKey key) {
        byte[] bytes = EncryptionUtils.encrypt(data, key.toSecretKey());
        return new AuthToken(bytes);
    }

    /** Creates a token from URL-friendly base64 text. */
    public static AuthToken fromString(String value) {
        return new AuthToken(value);
    }

    /** Decrypts the token to get the authentication data. */
    public byte[] decrypt(AuthKey key) {
        return EncryptionUtils.decrypt(bytes, key.toSecretKey());
    }

    private AuthToken(byte[] bytes) {
        super(bytes);
    }

    private AuthToken(String value) {
        super(value);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<AuthToken> {

        public Deserializer() {
            super(AuthToken.class, AuthToken::fromString);
        }
    }
}
