package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
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
}
