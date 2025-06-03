package org.example.age.service.testing;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import org.example.age.api.AvsApi;
import org.example.age.service.AvsServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li><code>@Named("testService") {@link AvsApi}</code>
 *     <li>{@link AvsServiceConfig}
 * </ul>
 * <p>
 * Depends on an unbound <code>@Named("service") {@link AvsApi}</code>.
 */
@Module
interface TestAvsDependenciesModule {

    @Binds
    @Named("testService")
    AvsApi bindAvsTestService(TestWrappedAvsService impl);

    @Provides
    static AvsServiceConfig provideAvsServiceConfig() {
        return TestConfig.avsService();
    }
}
