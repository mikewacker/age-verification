package org.example.age.site.api.data.internal;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;
import org.example.age.data.mapper.DataMapper;

/** Dagger module that publishes a binding for <code>@Named("api") {@link JsonSerializer}</code>. */
@Module
public interface SiteApiJsonSerializerModule {

    @Provides
    @Named("api")
    @Singleton
    static JsonSerializer provideApiJsonSerializer() {
        return JsonSerializer.create(DataMapper.get());
    }
}
