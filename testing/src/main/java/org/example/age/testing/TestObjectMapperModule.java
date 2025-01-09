package org.example.age.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;

/** Dagger module that binds {@link ObjectMapper}. */
@Module
public interface TestObjectMapperModule {

    @Provides
    static ObjectMapper provideObjectMapper() {
        return TestObjectMapper.get();
    }
}
