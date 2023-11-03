package org.example.age.common.avs.store;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import java.util.function.Consumer;
import javax.inject.Named;

/**
 * Dagger module that publishes a binding for {@link VerifiedUserStore}, which uses an in-memory store.
 *
 * <p>Depends on an unbound (optional) <code>@Named("initializer") Consumer&lt;{@link VerifiedUserStore}&gt;</code>.</p>
 */
@Module
public interface InMemoryVerifiedUserStoreModule {

    @Binds
    VerifiedUserStore bindVerifiedUserStore(InMemoryVerifiedUserStore impl);

    @BindsOptionalOf
    @Named("initializer")
    Consumer<VerifiedUserStore> bindOptionalVerifiedUserStoreInitializer();
}
