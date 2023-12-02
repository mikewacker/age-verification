package org.example.age.common.avs.store;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import java.util.function.Consumer;
import javax.inject.Named;

/**
 * Dagger module that publishes a binding for {@link RegisteredSiteConfigStore}, which uses an in-memory store.
 *
 * <p>Depends on an unbound (optional)
 * <code>@Named("initializer") Consumer&lt;{@link RegisteredSiteConfigStore}&gt;</code>.</p>
 */
@Module
public interface InMemoryRegisteredSiteConfigStoreModule {

    @Binds
    RegisteredSiteConfigStore bindSiteConfigStore(InMemoryRegisteredSiteConfigStore impl);

    @BindsOptionalOf
    @Named("initializer")
    Consumer<RegisteredSiteConfigStore> bindOptionalRegisteredSiteConfigStoreInitializer();
}
