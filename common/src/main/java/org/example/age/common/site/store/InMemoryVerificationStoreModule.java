package org.example.age.common.site.store;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import java.util.function.Consumer;
import javax.inject.Named;

/**
 * Dagger module that publishes a binding for {@link VerificationStore}, which uses an in-memory store.
 *
 * <p>Depends on an unbound (optional) <code>@Named("initializer") Consumer&lt;{@link VerificationStore}&gt;</code>.</p>
 */
@Module
public interface InMemoryVerificationStoreModule {

    @Binds
    VerificationStore bindVerificationStateStore(InMemoryVerificationStore impl);

    @BindsOptionalOf
    @Named("initializer")
    Consumer<VerificationStore> bindOptionalConsumerVerificationStoreInitializer();
}
