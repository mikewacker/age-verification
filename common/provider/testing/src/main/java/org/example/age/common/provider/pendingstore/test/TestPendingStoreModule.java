package org.example.age.common.provider.pendingstore.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.spi.PendingStoreRepository;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Key-value pairs do not expire.
 */
@Module
public abstract class TestPendingStoreModule {

    @Binds
    abstract PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);

    TestPendingStoreModule() {}
}
