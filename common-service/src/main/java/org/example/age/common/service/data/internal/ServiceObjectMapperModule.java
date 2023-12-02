package org.example.age.common.service.data.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.data.utils.DataMapper;

/** Dagger module that publishes a binding for <code>@Named("service") {@link ObjectMapper}</code>. */
@Module
public interface ServiceObjectMapperModule {

    @Provides
    @Named("service")
    @Singleton
    static ObjectMapper provideServiceObjectMapper() {
        return DataMapper.get();
    }
}
