package org.example.age.avs.provider.userlocalizer.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;

/**
 * Dagger module that binds {@link AvsVerifiedUserLocalizer}.
 * <p>
 * Uses seeded keys.
 */
@Module
public abstract class TestAvsUserLocalizerModule {

    @Binds
    abstract AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(FakeAvsVerifiedUserLocalizer impl);

    TestAvsUserLocalizerModule() {}
}
