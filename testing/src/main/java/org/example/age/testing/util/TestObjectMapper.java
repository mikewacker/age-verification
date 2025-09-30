package org.example.age.testing.util;

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

    /** Serializes an object to JSON without checked exceptions. */
    public static String serialize(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Deserializes an object from JSON without checked exceptions. */
    public static <V> V deserialize(String json, Class<V> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private TestObjectMapper() {} // static class
}
