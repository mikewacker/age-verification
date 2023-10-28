package org.example.age.common.base.client.internal;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link ExchangeClient}. */
@Module
public interface ExchangeClientModule {

    @Binds
    ExchangeClient bindExchangeClient(ExchangeClientImpl impl);
}
