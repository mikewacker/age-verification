package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.example.age.internal.SerializationUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AuthTokenTest {

    private static final byte[] DATA = "auth-data".getBytes(StandardCharsets.UTF_8);

    private static AuthKey key;

    @BeforeAll
    public static void generateKeys() {
        key = AuthKey.generate();
    }

    @Test
    public void encryptThenSerializeThenDeserializeThenDecrypt() {
        AuthToken token = AuthToken.encrypt(DATA, key);
        byte[] bytes = SerializationUtils.serialize(token);
        AuthToken deserializedToken = SerializationUtils.deserialize(bytes, AuthToken.class);
        byte[] decryptedData = deserializedToken.decrypt(key);
        assertThat(decryptedData).isEqualTo(DATA);
    }

    @Test
    public void empty() {
        AuthToken token = AuthToken.empty();
        assertThat(token.isEmpty()).isTrue();
    }

    @Test
    public void error_Decrypt_EmptyToken() {
        AuthToken token = AuthToken.empty();
        assertThatThrownBy(() -> token.decrypt(key))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("token is empty");
    }
}
