package org.example.age.infra.api.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/**
 * Dagger module that publishes a binding for {@link JsonSerializer}.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module
public interface JsonSerializerModule {

    @Provides
    @Singleton
    static JsonSerializer provideJsonSerializer(ObjectMapper mapper) {
        return JsonSerializer.create(mapper);
    }
}
