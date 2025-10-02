package org.example.age.module.store.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.spi.PendingStoreRepository;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Key-value pairs do not expire.
 */
@Module
public interface TestPendingStoreModule {

    @Binds
    PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);
}
