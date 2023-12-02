package org.example.age.infra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/**
 * Dagger module that publishes a binding for <code>@Named("service") {@link JsonSerializer}</code>.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link ObjectMapper}</code>.</p>
 */
@Module
public interface ServiceJsonSerializerModule {

    @Provides
    @Named("service")
    @Singleton
    static JsonSerializer provideServiceJsonSerializer(@Named("service") ObjectMapper mapper) {
        return JsonSerializer.create(mapper);
    }
}
