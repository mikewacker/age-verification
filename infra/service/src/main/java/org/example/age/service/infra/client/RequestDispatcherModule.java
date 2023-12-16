package org.example.age.service.infra.client;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.infra.client.internal.DispatcherOkHttpClientModule;

/** Dagger module that publishes a binding for {@link RequestDispatcher}. */
@Module(includes = DispatcherOkHttpClientModule.class)
public interface RequestDispatcherModule {

    @Binds
    RequestDispatcher bindRequestDispatcher(RequestDispatcherImpl impl);
}
