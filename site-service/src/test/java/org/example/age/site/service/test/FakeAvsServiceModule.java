package org.example.age.site.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import java.security.PrivateKey;
import javax.inject.Singleton;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.api.AvsApiModule;
import org.example.age.common.service.data.DisabledAuthMatchDataExtractorModule;
import org.example.age.data.utils.DataMapper;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.site.api.AvsLocation;
import org.example.age.test.service.data.TestAccountIdExtractorModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AvsLocation}</li>
 *     <li><code>@Named("signing") {@link PrivateKey}</code></li>
 * </ul>
 */
@Module(
        includes = {
            AvsApiModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            RequestDispatcherModule.class,
        })
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return DataMapper.get();
    }
}
