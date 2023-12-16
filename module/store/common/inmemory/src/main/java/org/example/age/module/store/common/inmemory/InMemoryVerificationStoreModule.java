package org.example.age.module.store.common.inmemory;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.store.common.VerificationStore;

/** Dagger module that publishes a binding for {@link VerificationStore}, which uses an in-memory store. */
@Module
public interface InMemoryVerificationStoreModule {

    @Binds
    VerificationStore bindVerificationStateStore(InMemoryVerificationStore impl);
}
