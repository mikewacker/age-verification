package org.example.age.module.store.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.AvsVerifiedUserStore;

/**
 * Dagger module that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Depends on an unbound {@link AvsStoresConfig}.
 * <p>
 * Verified accounts are loaded from configuration.
 */
@Module
public interface DemoAvsAccountStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(DemoAvsVerifiedUserStore impl);
}
