package org.example.age.module.store.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.PendingStoreRepository;

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
