package org.example.age.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class JsonSerializerTest {

    private static JsonSerializer serializer;

    @BeforeAll
    public static void createJsonSerializer() {
        serializer = JsonSerializer.create(new ObjectMapper());
    }

    @Test
    public void serializeThenDeserialize() {
        String value = "test";
        byte[] rawValue = serializer.serialize(value);
        HttpOptional<String> maybeRtValue = serializer.tryDeserialize(rawValue, new TypeReference<>() {}, 400);
        assertThat(maybeRtValue.isPresent()).isTrue();
        assertThat(maybeRtValue.get()).isEqualTo(value);
    }

    @Test
    public void deserializeFailed() {
        byte[] rawValue = new byte[4];
        HttpOptional<String> maybeValue = serializer.tryDeserialize(rawValue, new TypeReference<>() {}, 400);
        assertThat(maybeValue.isEmpty()).isTrue();
        assertThat(maybeValue.statusCode()).isEqualTo(400);
    }
}
