package org.example.age.common.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;

/** Singleton JSON object mapper for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    /** Gets the JSON object mapper. */
    public static ObjectMapper get() {
        return mapper;
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

    private TestObjectMapper() {} // static class
}
