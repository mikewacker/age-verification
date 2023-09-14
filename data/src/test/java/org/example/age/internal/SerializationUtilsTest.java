package org.example.age.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public final class SerializationUtilsTest {

    @Test
    public void serializeThenDeserialize() {
        String text = "hello";
        byte[] bytes = SerializationUtils.serialize(text);
        String deserializedText = SerializationUtils.deserialize(bytes, String.class);
        assertThat(deserializedText).isEqualTo(text);
    }

    @Test
    public void error_Deserialize() {
        byte[] bytes = new byte[4];
        assertThatThrownBy(() -> SerializationUtils.deserialize(bytes, String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deserialization failed");
    }
}
