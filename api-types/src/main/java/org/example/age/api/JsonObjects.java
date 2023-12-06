package org.example.age.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Serializes and deserializes objects to and from JSON.
 *
 * <p>Types should be serializable using the default object mapper, which has no registered modules.</p>
 */
public final class JsonObjects {

    private static final ObjectMapper mapper = new ObjectMapper();

    /** Serializes an object to JSON. */
    public static byte[] serialize(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("serialization failed", e);
        }
    }

    /**
     * Deserializes an object from JSON.
     *
     * <p>Typically used for deserializing internal data, which should be well-formed.</p>
     */
    public static <V> V deserialize(byte[] rawValue, TypeReference<V> valueTypeRef) {
        try {
            return mapper.readValue(rawValue, valueTypeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException("deserialization failed", e);
        }
    }

    /**
     * Deserializes an object from JSON, or returns an error status code.
     *
     * <p>Typically used for deserializing user data, which may be malformed.</p>
     */
    public static <V> HttpOptional<V> tryDeserialize(byte[] rawValue, TypeReference<V> valueTypeRef, int errorCode) {
        try {
            V value = mapper.readValue(rawValue, valueTypeRef);
            return HttpOptional.of(value);
        } catch (IOException e) {
            return HttpOptional.empty(errorCode);
        }
    }

    // static class
    private JsonObjects() {}
}
