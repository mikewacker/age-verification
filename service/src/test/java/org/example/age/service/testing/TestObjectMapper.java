package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** {@link ObjectMapper} singleton for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = create();

    /** Gets the {@link ObjectMapper}. */
    public static ObjectMapper get() {
        return mapper;
    }

    /** Creates the {@link ObjectMapper}. */
    private static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    // static class
    private TestObjectMapper() {}
}
