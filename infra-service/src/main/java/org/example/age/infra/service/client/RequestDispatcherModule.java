package org.example.age.infra.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.infra.api.data.JsonSerializerModule;
import org.example.age.infra.service.client.internal.ExchangeClientModule;

/**
 * Dagger module that publishes a binding for {@link RequestDispatcher}.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module(includes = {ExchangeClientModule.class, JsonSerializerModule.class})
public interface RequestDispatcherModule {

    @Binds
    RequestDispatcher bindRequestDispatcher(RequestDispatcherImpl impl);
}
