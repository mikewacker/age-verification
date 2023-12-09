package org.example.age.infra.service.client;

import dagger.Binds;
import dagger.Module;
import org.example.age.data.json.JsonValues;
import org.example.age.infra.service.client.internal.ExchangeClientModule;

/**
 * Dagger module that publishes a binding for {@link RequestDispatcher}.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link JsonValues}</code>.</p>
 */
@Module(includes = ExchangeClientModule.class)
public interface RequestDispatcherModule {

    @Binds
    RequestDispatcher bindRequestDispatcher(RequestDispatcherImpl impl);
}
