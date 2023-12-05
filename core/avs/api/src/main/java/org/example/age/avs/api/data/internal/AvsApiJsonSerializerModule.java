package org.example.age.avs.api.data.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/** Dagger module that publishes a binding for <code>@Named("api") {@link JsonSerializer}</code>. */
@Module
public interface AvsApiJsonSerializerModule {

    @Provides
    @Named("api")
    @Singleton
    static JsonSerializer provideApiJsonSerializer() {
        return JsonSerializer.create(new ObjectMapper());
    }
}
