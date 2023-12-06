package org.example.age.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

public final class JsonSerializerTest {

    @Test
    public void serializeThenDeserialize() {
        String value = "test";
        byte[] rawValue = JsonSerializer.serialize(value);
        String rtValue = JsonSerializer.deserialize(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }

    @Test
    public void serializeThenTryDeserialize() {
        String value = "test";
        byte[] rawValue = JsonSerializer.serialize(value);
        HttpOptional<String> maybeRtValue = JsonSerializer.tryDeserialize(rawValue, new TypeReference<>() {}, 400);
        assertThat(maybeRtValue).hasValue(value);
    }

    @Test
    public void serializeFailed() {
        Object unserializableValue = new JsonSerializerTest();
        assertThatThrownBy(() -> JsonSerializer.serialize(unserializableValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("serialization failed");
    }

    @Test
    public void deserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        assertThatThrownBy(() -> JsonSerializer.deserialize(malformedRawValue, new TypeReference<>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deserialization failed");
    }

    @Test
    public void tryDeserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        HttpOptional<String> maybeValue =
                JsonSerializer.tryDeserialize(malformedRawValue, new TypeReference<>() {}, 400);
        assertThat(maybeValue).isEmptyWithErrorCode(400);
    }
}
