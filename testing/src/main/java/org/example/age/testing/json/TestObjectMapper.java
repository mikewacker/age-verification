package org.example.age.testing.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;

/** Singleton JSON object mapper for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = create();

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

    /** Creates the JSON object mapper. */
    private static ObjectMapper create() {
        ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private TestObjectMapper() {} // static class
}
