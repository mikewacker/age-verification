package org.example.age.avs.service.data.internal;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;
import org.example.age.data.mapper.DataMapper;

/** Dagger module that publishes a binding for <code>@Named("service") {@link JsonSerializer}</code>. */
@Module
public interface AvsServiceJsonSerializerModule {

    @Provides
    @Named("service")
    @Singleton
    static JsonSerializer provideServiceJsonSerializer() {
        return JsonSerializer.create(DataMapper.get());
    }
}
