package org.example.age.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.age.internal.SerializationUtils;
import org.junit.jupiter.api.Test;

public final class SecureIdTest {

    @Test
    public void generate() {
        SecureId id = SecureId.generate();
        assertThat(id.bytes()).hasSize(32);
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
        byte[] bytes = SerializationUtils.serialize(id);
        SecureId deserializedId = SerializationUtils.deserialize(bytes, SecureId.class);
        assertThat(deserializedId).isEqualTo(id);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> SecureId.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
