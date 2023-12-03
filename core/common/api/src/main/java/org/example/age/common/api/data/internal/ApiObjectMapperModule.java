package org.example.age.common.api.data.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.data.mapper.DataMapper;

/** Dagger module that publishes a binding for <code>@Named("api") {@link ObjectMapper}</code>. */
@Module
public interface ApiObjectMapperModule {

    @Provides
    @Named("api")
    @Singleton
    static ObjectMapper provideApiObjectMapper() {
        return DataMapper.get();
    }
}
