package org.example.age.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/** {@link ObjectMapper} for all types that can be serialized as JSON. */
public final class DataMapper {

    private static final ObjectMapper mapper = createObjectMapper();

    /** Gets the {@link ObjectMapper} singleton. */
    public static ObjectMapper get() {
        return mapper;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        return mapper;
    }

    // static class
    private DataMapper() {}
}
