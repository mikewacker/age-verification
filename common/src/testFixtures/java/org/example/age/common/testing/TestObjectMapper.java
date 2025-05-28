package org.example.age.common.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

/** Singleton JSON object mapper for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    /** Gets the JSON object mapper. */
    public static ObjectMapper get() {
        return mapper;
    }

    private TestObjectMapper() {} // static class
}
