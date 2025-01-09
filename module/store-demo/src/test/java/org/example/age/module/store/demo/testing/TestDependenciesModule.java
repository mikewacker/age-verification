package org.example.age.module.store.demo.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.store.demo.AvsStoresConfig;

/** Dagger module that binds {@link AvsStoresConfig}. */
@Module
public interface TestDependenciesModule {

    @Provides
    static AvsStoresConfig provideAvsStoresConfig() {
        return TestConfig.avs();
    }
}
