package org.example.age.api.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public final class SecureIdTest {

    @Test
    public void generate() {
        SecureId id = SecureId.generate();
        assertThat(id.getBytes()).hasSize(32);
    }

    @Test
    public void localize() {
        SecureId id = SecureId.generate();
        SecureId key = SecureId.generate();
        SecureId localizedId = id.localize(key);
        assertThat(localizedId).isNotEqualTo(id);
        assertThat(localizedId.getBytes()).hasSize(32);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SecureId id = SecureId.generate();
        String json = mapper.writeValueAsString(id);
        SecureId rtId = mapper.readValue(json, SecureId.class);
        assertThat(rtId).isEqualTo(id);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> SecureId.fromString("AAAA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SecureId must have 256 bits");
    }
}
