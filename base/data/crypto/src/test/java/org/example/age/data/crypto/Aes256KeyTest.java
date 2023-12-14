package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class Aes256KeyTest {

    @Test
    public void generate() {
        Aes256Key key = Aes256Key.generate();
        assertThat(key.bytes()).hasSize(32);
        assertThat(key.bytes()).isNotEqualTo(new byte[32]);
    }

    @Test
    public void serializeThenDeserialize() {
        Aes256Key key = Aes256Key.generate();
        byte[] rawKey = JsonValues.serialize(key);
        Aes256Key rtKey = JsonValues.deserialize(rawKey, new TypeReference<>() {});
        assertThat(rtKey).isEqualTo(key);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> SecureId.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
