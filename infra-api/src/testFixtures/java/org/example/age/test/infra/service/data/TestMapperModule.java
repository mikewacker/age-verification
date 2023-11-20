package org.example.age.test.infra.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Dagger module that publishes a binding for {@link ObjectMapper}. */
@Module
public interface TestMapperModule {

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }
}
