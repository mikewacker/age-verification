package org.example.age.data.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.example.age.data.DataMapper;

/** Utilities for serializing and deserializing objects to and from bytes. */
public final class SerializationUtils {

    private static final ObjectMapper mapper = DataMapper.get();

    /** Serializes an object to bytes. */
    public static byte[] serialize(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes bytes to an object, or throws an {@link IllegalArgumentException}. */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("deserialization failed", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // static class
    private SerializationUtils() {}
}
