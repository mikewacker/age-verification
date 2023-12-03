package org.example.age.api;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

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
        assertThat(maybeRtValue).hasValue(value);
    }

    @Test
    public void deserializeFailed() {
        byte[] rawValue = new byte[4];
        HttpOptional<String> maybeValue = serializer.tryDeserialize(rawValue, new TypeReference<>() {}, 400);
        assertThat(maybeValue).isEmptyWithErrorCode(400);
    }
}
