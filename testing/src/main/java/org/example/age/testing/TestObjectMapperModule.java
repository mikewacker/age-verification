package org.example.age.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

/** Dagger module that binds {@link ObjectMapper}. */
@Module
public interface TestObjectMapperModule {

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return TestObjectMapper.get();
    }
}
