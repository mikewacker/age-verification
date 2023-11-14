package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.age.data.internal.SerializationUtils;
import org.junit.jupiter.api.Test;

public final class AuthKeyTest {

    @Test
    public void generate() {
        AuthKey key = AuthKey.generate();
        assertThat(key.bytes()).hasSize(32);
    }

    @Test
    public void serializeThenDeserialize() {
        AuthKey key = AuthKey.generate();
        byte[] bytes = SerializationUtils.serialize(key);
        AuthKey deserializedKey = SerializationUtils.deserialize(bytes, AuthKey.class);
        assertThat(deserializedKey).isEqualTo(key);
    }

    @Test
    public void error_IllegalLength() {
        assertThatThrownBy(() -> AuthKey.ofBytes(new byte[4]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }
}
