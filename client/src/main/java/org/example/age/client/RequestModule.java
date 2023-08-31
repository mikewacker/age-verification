package org.example.age.client;

import dagger.Binds;
import dagger.Module;

/** Publishes a binding for {@link RequestDispatcher}. */
@Module(includes = ClientModule.class)
public interface RequestModule {

    @Binds
    RequestDispatcher bindRequestDispatcher(RequestDispatcherImpl impl);
}
