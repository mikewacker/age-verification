package org.example.age.client;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link HttpServerExchangeClient}. */
@Module
public interface ClientModule {

    @Binds
    HttpServerExchangeClient bindHttpExchangeClient(HttpServerExchangeClientImpl impl);
}
