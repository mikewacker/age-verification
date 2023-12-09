package org.example.age.api.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class JsonValuesTest {

    @Test
    public void serializeThenDeserialize() {
        String value = "test";
        byte[] rawValue = JsonValues.serialize(value);
        String rtValue = JsonValues.deserialize(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }

    @Test
    public void serializeThenTryDeserialize() {
        String value = "test";
        byte[] rawValue = JsonValues.serialize(value);
        Optional<String> maybeRtValue = JsonValues.tryDeserialize(rawValue, new TypeReference<>() {});
        assertThat(maybeRtValue).hasValue(value);
    }

    @Test
    public void serializeBytesUsingUrlFriendlyBase64Encoding() {
        byte[] value = new byte[] {-5, -16, 0, 0};
        byte[] rawValue = JsonValues.serialize(value);
        assertThat(new String(rawValue, StandardCharsets.UTF_8)).isEqualTo("\"-_AAAA\"");
    }

    @Test
    public void serializeFailed() {
        Object unserializableValue = new JsonValuesTest();
        assertThatThrownBy(() -> JsonValues.serialize(unserializableValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("serialization failed");
    }

    @Test
    public void deserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        assertThatThrownBy(() -> JsonValues.deserialize(malformedRawValue, new TypeReference<>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deserialization failed");
    }

    @Test
    public void tryDeserializeFailed() {
        byte[] malformedRawValue = new byte[4];
        Optional<String> maybeValue = JsonValues.tryDeserialize(malformedRawValue, new TypeReference<>() {});
        assertThat(maybeValue).isEmpty();
    }
}
