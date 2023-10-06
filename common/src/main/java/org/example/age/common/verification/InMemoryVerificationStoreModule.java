package org.example.age.common.verification;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link VerificationStore}, which uses an in-memory store. */
@Module
public interface InMemoryVerificationStoreModule {

    @Binds
    VerificationStore bindVerificationStateStore(InMemoryVerificationStore impl);
}
