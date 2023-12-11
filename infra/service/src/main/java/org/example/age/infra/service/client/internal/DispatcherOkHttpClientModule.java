package org.example.age.infra.service.client.internal;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link DispatcherOkHttpClient}. */
@Module
public interface DispatcherOkHttpClientModule {

    @Binds
    DispatcherOkHttpClient bindExchangeClient(DispatcherOkHttpClientImpl impl);
}
