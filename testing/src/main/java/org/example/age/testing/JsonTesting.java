package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

/** Test utilities for JSON serialization and deserialization. */
public final class JsonTesting {

    /** Serializes then deserializes an object, asserting that the deserialized object equals the original object. */
    public static <V> void serializeThenDeserialize(V value, Class<V> valueType) throws IOException {
        String json = TestObjectMapper.get().writeValueAsString(value);
        V rtValue = TestObjectMapper.get().readValue(json, valueType);
        assertThat(rtValue).isEqualTo(value);
    }

    /** Serializes an object to JSON. */
    public static String serialize(Object value) {
        try {
            return TestObjectMapper.get().writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes an object from JSON. */
    public static <V> V deserialize(String json, Class<V> valueType) {
        try {
            return TestObjectMapper.get().readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonTesting() {} // static class
}
