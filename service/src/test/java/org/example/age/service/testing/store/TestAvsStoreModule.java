package org.example.age.service.testing.store;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.PendingStoreRepository;

/**
 * Dagger modules that binds...
 * <ul>
 *     <li>{@link AvsVerifiedUserStore}
 *     <li>{@link PendingStoreRepository}
 * </ul>
 */
@Module
public interface TestAvsStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(FakeAvsVerifiedUserStore impl);

    @Binds
    PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);
}
