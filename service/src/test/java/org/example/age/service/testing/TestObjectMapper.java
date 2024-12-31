package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** {@link ObjectMapper} for testing. */
final class TestObjectMapper {

    /** Creates the {@link ObjectMapper}. */
    public static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    // static class
    private TestObjectMapper() {}
}
