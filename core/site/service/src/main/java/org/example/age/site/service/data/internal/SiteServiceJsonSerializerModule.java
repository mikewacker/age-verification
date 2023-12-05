package org.example.age.site.service.data.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/** Dagger module that publishes a binding for <code>@Named("service") {@link JsonSerializer}</code>. */
@Module
public interface SiteServiceJsonSerializerModule {

    @Provides
    @Named("service")
    @Singleton
    static JsonSerializer provideServiceJsonSerializer() {
        return JsonSerializer.create(new ObjectMapper());
    }
}
