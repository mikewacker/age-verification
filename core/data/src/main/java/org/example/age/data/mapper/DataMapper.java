package org.example.age.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/** {@link ObjectMapper} for all types that can be serialized as JSON. */
public final class DataMapper {

    private static final ObjectMapper mapper = create();

    /** Gets the {@link ObjectMapper} singleton. */
    public static ObjectMapper get() {
        return mapper;
    }

    /** Creates the {@link ObjectMapper} singleton. */
    private static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        return mapper;
    }

    // static class
    private DataMapper() {}
}
