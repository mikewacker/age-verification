package org.example.age.module.store.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserStore;

/**
 * Dagger modules that binds {@link AvsVerifiedUserStore}.
 * <p>
 * It has one account with an ID of "person".
 */
@Module
public interface TestAvsAccountStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(FakeAvsVerifiedUserStore impl);
}
