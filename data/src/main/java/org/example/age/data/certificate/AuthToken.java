package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Optional;
import org.example.age.data.crypto.EncryptionUtils;
import org.example.age.data.internal.ImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/**
 * Encrypted data used to assist with authentication. Can also be empty.
 *
 * <p>The data could contain something such as an IP address, which is why it is encrypted.</p>
 */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = AuthToken.Deserializer.class)
public final class AuthToken extends ImmutableBytes {

    /** Creates a token by encrypting authentication data. */
    public static AuthToken encrypt(byte[] data, AuthKey key) {
        byte[] iv = EncryptionUtils.createIv();
        byte[] ciphertext = EncryptionUtils.encrypt(data, key.bytes(), iv);
        byte[] rawToken = PackageUtils.createEncryptionPackage(ciphertext, iv);
        return new AuthToken(rawToken);
    }

    /** Creates an empty token. */
    public static AuthToken empty() {
        // 0 bytes triggers a deserialization error. (An encrypted token has at least 12 bytes for the IV.)
        return new AuthToken(new byte[1]);
    }

    /** Creates a token from URL-friendly base64 text. */
    public static AuthToken fromString(String value) {
        return new AuthToken(value);
    }

    /** Determines if the token is empty. */
    public boolean isEmpty() {
        return bytes.length == 1;
    }

    /** Decrypts the token to get the authentication data. */
    public byte[] decrypt(AuthKey key) {
        checkNotEmpty();
        PackageUtils.EncryptionPackage token = PackageUtils.parseEncryptionPackage(bytes);
        Optional<byte[]> maybeData = EncryptionUtils.tryDecrypt(token.ciphertext(), key.bytes(), token.iv());
        if (maybeData.isEmpty()) {
            throw new IllegalArgumentException("decryption failed");
        }
        return maybeData.get();
    }

    private AuthToken(byte[] bytes) {
        super(bytes);
    }

    private AuthToken(String value) {
        super(value);
    }

    /** Checks that the token is not empty. */
    private void checkNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("token is empty");
        }
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<AuthToken> {

        public Deserializer() {
            super(AuthToken.class, AuthToken::fromString);
        }
    }
}
