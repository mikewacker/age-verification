package org.example.age.avs.provider.accountstore.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedAccountStore;

/**
 * Dagger modules that binds {@link AvsVerifiedAccountStore}.
 * <p>
 * Uses seeded data.
 */
@Module
public abstract class TestAvsAccountStoreModule {

    @Binds
    abstract AvsVerifiedAccountStore bindAvsVerifiedAccountStore(FakeAvsVerifiedAccountStore impl);

    TestAvsAccountStoreModule() {}
}
