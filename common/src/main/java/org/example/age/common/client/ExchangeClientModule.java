package org.example.age.common.client;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link ExchangeClient}. */
@Module
interface ExchangeClientModule {

    @Binds
    ExchangeClient bindExchangeClient(ExchangeClientImpl impl);
}
