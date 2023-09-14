package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
    public void encryptThenSerializeThenDeserializeThenDecrypt() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AuthToken token = AuthToken.encrypt(DATA, key);
        String json = mapper.writeValueAsString(token);
        AuthToken deserializedToken = mapper.readValue(json, AuthToken.class);
        byte[] decryptedData = deserializedToken.decrypt(key);
        assertThat(decryptedData).isEqualTo(DATA);
    }
}
