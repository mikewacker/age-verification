package org.example.age.avs.provider.accountstore.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserStore;

/**
 * Dagger modules that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Uses seeded data.
 */
@Module
public abstract class TestAvsAccountStoreModule {

    @Binds
    abstract AvsVerifiedUserStore bindAvsVerifiedUserStore(FakeAvsVerifiedUserStore impl);

    TestAvsAccountStoreModule() {}
}
