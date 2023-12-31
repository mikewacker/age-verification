package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
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
        JsonTester.serializeThenDeserialize(Aes256Key.generate(), new TypeReference<>() {});
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> SecureId.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
