package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.example.age.testing.TestEncrypting;
import org.junit.jupiter.api.Test;

public final class AuthKeyTest {

    private static final byte[] DATA = "auth-data".getBytes(StandardCharsets.UTF_8);

    @Test
    public void generate() {
        AuthKey key = AuthKey.generate();
        assertThat(key.bytes()).hasSize(32);
    }

    @Test
    public void toSecretKey() throws Exception {
        AuthKey key = AuthKey.generate();
        byte[] iv = TestEncrypting.createGcmIv();
        byte[] ciphertext = TestEncrypting.encryptAesGcm(DATA, key.toSecretKey(), iv);
        byte[] plaintext = TestEncrypting.decryptAesGcm(ciphertext, key.toSecretKey(), iv);
        assertThat(plaintext).isEqualTo(DATA);
    }

    @Test
    public void serializeThenDeserialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AuthKey key = AuthKey.generate();
        String json = mapper.writeValueAsString(key);
        AuthKey deserializedKey = mapper.readValue(json, AuthKey.class);
        assertThat(deserializedKey).isEqualTo(key);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> AuthKey.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
