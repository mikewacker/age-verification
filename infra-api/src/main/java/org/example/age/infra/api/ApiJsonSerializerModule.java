package org.example.age.infra.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link JsonSerializer}</code>.
 *
 * <p>Depends on an unbound <code>@Named("api") {@link ObjectMapper}</code>.</p>
 */
@Module
public interface ApiJsonSerializerModule {

    @Provides
    @Named("api")
    @Singleton
    static JsonSerializer provideApiJsonSerializer(@Named("api") ObjectMapper mapper) {
        return JsonSerializer.create(mapper);
    }
}
