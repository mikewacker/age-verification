package org.example.age.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public final class JsonObjectsTest {

    @Test
    public void serializeThenDeserialize() {
        String value = "test";
        byte[] rawValue = JsonObjects.serialize(value);
        String rtValue = JsonObjects.deserialize(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }

    @Test
    public void serializeThenTryDeserialize() {
        String value = "test";
        byte[] rawValue = JsonObjects.serialize(value);
        HttpOptional<String> maybeRtValue = JsonObjects.tryDeserialize(rawValue, new TypeReference<>() {}, 400);
        assertThat(maybeRtValue).hasValue(value);
    }

    @Test
    public void serializeBytesUsingUrlFriendlyBase64Encoding() {
        byte[] value = new byte[] {-5, -16, 0, 0};
        byte[] rawValue = JsonObjects.serialize(value);
        assertThat(new String(rawValue, StandardCharsets.UTF_8)).isEqualTo("\"-_AAAA\"");
    }

    @Test
    public void serializeFailed() {
        Object unserializableValue = new JsonObjectsTest();
        assertThatThrownBy(() -> JsonObjects.serialize(unserializableValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("serialization failed");
    }

    @Test
    public void deserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        assertThatThrownBy(() -> JsonObjects.deserialize(malformedRawValue, new TypeReference<>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deserialization failed");
    }

    @Test
    public void tryDeserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        HttpOptional<String> maybeValue = JsonObjects.tryDeserialize(malformedRawValue, new TypeReference<>() {}, 400);
        assertThat(maybeValue).isEmptyWithErrorCode(400);
    }
}
