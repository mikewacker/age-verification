package org.example.age.common.service.store.inmemory;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.store.VerificationStore;

/** Dagger module that publishes a binding for {@link VerificationStore}, which uses an in-memory store. */
@Module
public interface InMemoryVerificationStoreModule {

    @Binds
    VerificationStore bindVerificationStateStore(InMemoryVerificationStore impl);
}
