package org.example.age.data.json;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;

/**
 * Serializes and deserializes objects to and from JSON, using a URL-friendly base64 encoding for {@code byte[]} values.
 *
 * <p>Types should be serializable using the default object mapper, which has no registered modules.</p>
 */
public final class JsonValues {

    private static final ObjectMapper mapper = new ObjectMapper().setBase64Variant(Base64Variants.MODIFIED_FOR_URL);

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
     * Deserializes an object from JSON, or returns empty.
     *
     * <p>Typically used for deserializing user data, which may be malformed.</p>
     */
    public static <V> Optional<V> tryDeserialize(byte[] rawValue, TypeReference<V> valueTypeRef) {
        try {
            V value = mapper.readValue(rawValue, valueTypeRef);
            return Optional.of(value);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    // static class
    private JsonValues() {}
}
