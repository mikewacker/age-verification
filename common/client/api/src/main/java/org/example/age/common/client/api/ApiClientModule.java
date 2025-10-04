package org.example.age.common.client.api;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link ApiClientFactory}.
 * <p>
 * Depends on an unbound {@link LiteEnv}.
 */
@Module
public abstract class ApiClientModule {

    @Binds
    abstract ApiClientFactory bindApiClientFactory(ApiClientFactoryImpl impl);

    ApiClientModule() {}
}
