package org.example.age.module.store.inmemory;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link VerificationStore},
 * which is an in-memory store without persistence.
 *
 * <p>Depends on an (optional) unbound {@link VerificationStoreInitializer}.</p>
 */
@Module
public interface InMemoryVerificationStoreModule {

    @Binds
    VerificationStore bindVerificationStateStore(InMemoryVerificationStore impl);

    @BindsOptionalOf
    VerificationStoreInitializer bindOptionalVerificationStoreInitializer();
}
