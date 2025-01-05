package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

/** Test utilities for JSON serialization and deserialization. */
public final class JsonTesting {

    /** Serializes then deserializes an object, verifying that the deserialized object equals the original object. */
    public static <V> void serializeThenDeserialize(V value, Class<V> valueType) throws IOException {
        String json = TestObjectMapper.get().writeValueAsString(value);
        V rtValue = TestObjectMapper.get().readValue(json, valueType);
        assertThat(rtValue).isEqualTo(value);
    }

    // static class
    private JsonTesting() {}
}
