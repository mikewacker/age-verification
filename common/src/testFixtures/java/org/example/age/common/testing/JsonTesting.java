package org.example.age.common.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

/** Test utilities for JSON serialization and deserialization. */
public final class JsonTesting {

    /** Serializes then deserializes an object, asserting that the deserialized object equals the original object. */
    public static <V> void serializeThenDeserialize(V value, Class<V> valueType) throws IOException {
        String json = serializeInternal(value);
        V rtValue = deserializeInternal(json, valueType);
        assertThat(rtValue).isEqualTo(value);
    }

    /** Serializes an object to JSON. */
    public static String serialize(Object value) {
        try {
            return serializeInternal(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes an object from JSON. */
    public static <V> V deserialize(String json, Class<V> valueType) {
        try {
            return deserializeInternal(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Serializes an object to JSON. */
    private static String serializeInternal(Object value) throws IOException {
        return TestObjectMapper.get().writeValueAsString(value);
    }

    /** Deserializes an object from JSON. */
    private static <V> V deserializeInternal(String json, Class<V> valueType) throws IOException {
        return TestObjectMapper.get().readValue(json, valueType);
    }

    private JsonTesting() {} // static class
}
