package org.example.age.service.testing.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.service.api.store.AvsVerifiedUserStore;
import org.example.age.service.api.store.PendingStoreRepository;
import org.example.age.service.api.store.SiteVerificationStore;

/**
 * Dagger modules that binds...
 * <ul>
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link AvsVerifiedUserStore}
 *     <li>{@link PendingStoreRepository}
 * </ul>
 * <p>
 * Depends on an unbound {@link ObjectMapper}.
 */
@Module
public interface TestStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(FakeSiteVerificationStore impl);

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(FakeAvsVerifiedUserStore impl);

    @Binds
    PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);
}
