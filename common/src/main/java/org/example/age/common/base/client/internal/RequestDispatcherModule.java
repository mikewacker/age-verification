package org.example.age.common.base.client.internal;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link RequestDispatcher}. */
@Module(includes = ExchangeClientModule.class)
public interface RequestDispatcherModule {

    @Binds
    RequestDispatcher bindRequestDispatcher(RequestDispatcherImpl impl);
}
