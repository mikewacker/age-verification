package org.example.age.common.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.data.utils.DataMapper;

/** Dagger module that publishes a binding for {@link ObjectMapper}. */
@Module
public interface DataMapperModule {

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return DataMapper.get();
    }
}
