package org.example.age.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/** Serializes and deserializes objects to and from JSON. */
public final class JsonSerializer {

    private final ObjectMapper mapper;

    /** Creates a serializer from the {@link ObjectMapper}. */
    public static JsonSerializer create(ObjectMapper mapper) {
        return new JsonSerializer(mapper);
    }

    /** Serializes an object to JSON. */
    public byte[] serialize(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }

    /** Deserializes an object from JSON, or returns an error status code. */
    public <V> HttpOptional<V> tryDeserialize(byte[] rawValue, TypeReference<V> valueTypeRef, int errorCode) {
        try {
            V value = mapper.readValue(rawValue, valueTypeRef);
            return HttpOptional.of(value);
        } catch (IOException e) {
            return HttpOptional.empty(errorCode);
        }
    }

    private JsonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
