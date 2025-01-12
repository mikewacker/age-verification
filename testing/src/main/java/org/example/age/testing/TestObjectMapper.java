package org.example.age.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;

/** {@link ObjectMapper} singleton for testing. */
public final class TestObjectMapper {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    /** Gets the {@link ObjectMapper}. */
    public static ObjectMapper get() {
        return mapper;
    }

    /** Dagger module that binds {@link ObjectMapper}. */
    @dagger.Module
    public interface Module {

        @Provides
        static ObjectMapper provideObjectMapper() {
            return get();
        }
    }

    // static class
    private TestObjectMapper() {}
}
