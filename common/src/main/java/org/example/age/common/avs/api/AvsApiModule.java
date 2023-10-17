package org.example.age.common.avs.api;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;

/** Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>. */
@Module
public interface AvsApiModule {

    @Binds
    @Named("api")
    HttpHandler bindApiHttpHandler(AvsApiHandler impl);
}
