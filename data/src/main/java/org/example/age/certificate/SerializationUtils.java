package org.example.age.certificate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;

/** Utilities for serializing and deserializing an {@link AgeCertificate}. */
final class SerializationUtils {

    private static final ObjectMapper mapper = createMapper();

    /** Serializes an age certificate to bytes. */
    public static byte[] serialize(AgeCertificate certificate) {
        try {
            return mapper.writeValueAsBytes(certificate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes bytes to an age certificate, or throws an {@link IllegalArgumentException}. */
    public static AgeCertificate deserialize(byte[] bytes, int offset, int length) {
        try {
            return mapper.readValue(bytes, offset, length, AgeCertificate.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("deserialization failed", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates a mapper for serialization and deserialization. */
    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new GuavaModule());
        return mapper;
    }

    // static class
    private SerializationUtils() {}
}
