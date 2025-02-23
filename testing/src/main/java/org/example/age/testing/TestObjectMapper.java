package org.example.age.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

/** {@link ObjectMapper} singleton for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    /** Gets the {@link ObjectMapper}. */
    public static ObjectMapper get() {
        return mapper;
    }

    // static class
    private TestObjectMapper() {}
}
