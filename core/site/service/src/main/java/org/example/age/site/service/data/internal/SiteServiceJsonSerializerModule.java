package org.example.age.site.service.data.internal;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;
import org.example.age.data.mapper.DataMapper;

/** Dagger module that publishes a binding for <code>@Named("service") {@link JsonSerializer}</code>. */
@Module
public interface SiteServiceJsonSerializerModule {

    @Provides
    @Named("service")
    @Singleton
    static JsonSerializer provideApiJsonSerializer() {
        return JsonSerializer.create(DataMapper.get());
    }
}
