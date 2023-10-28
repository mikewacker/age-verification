package org.example.age.common.avs.store;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link VerifiedUserStore}, which uses an in-memory store. */
@Module
public interface InMemoryVerifiedUserStoreModule {

    @Binds
    VerifiedUserStore bindVerifiedUserStore(InMemoryVerifiedUserStore impl);
}
