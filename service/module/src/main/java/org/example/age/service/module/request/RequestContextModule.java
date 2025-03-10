package org.example.age.service.module.request;

import dagger.Binds;
import dagger.Module;

/** Dagger module that binds {@link RequestContextProvider}. This provider must be registered with Jersey. */
@Module
public interface RequestContextModule {

    @Binds
    RequestContextProvider bindRequestContextProvider(RequestContextFilter impl);
}
