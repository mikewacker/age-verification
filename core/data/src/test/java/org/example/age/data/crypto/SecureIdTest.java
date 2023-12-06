package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.JsonObjects;
import org.junit.jupiter.api.Test;

public final class SecureIdTest {

    @Test
    public void generate() {
        SecureId id = SecureId.generate();
        assertThat(id.bytes()).hasSize(32);
        assertThat(id.bytes()).isNotEqualTo(new byte[32]);
    }

    @Test
    public void localize() {
        SecureId id = SecureId.generate();
        SecureId key = SecureId.generate();
        SecureId localId = id.localize(key);
        assertThat(localId).isNotEqualTo(id);
    }

    @Test
    public void serializeThenDeserialize() {
        SecureId id = SecureId.generate();
        byte[] rawId = JsonObjects.serialize(id);
        SecureId rtId = JsonObjects.deserialize(rawId, new TypeReference<>() {});
        assertThat(rtId).isEqualTo(id);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> SecureId.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
