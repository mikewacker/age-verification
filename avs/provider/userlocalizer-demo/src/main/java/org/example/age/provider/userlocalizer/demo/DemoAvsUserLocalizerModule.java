package org.example.age.provider.userlocalizer.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;

/**
 * Dagger module that binds {@link AvsVerifiedUserLocalizer}.
 * <p>
 * Depends on an unbound {@link AvsLocalizationKeysConfig}}.
 */
@Module
public abstract class DemoAvsUserLocalizerModule {

    @Binds
    abstract AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(DemoAvsVerifiedUserLocalizer impl);

    DemoAvsUserLocalizerModule() {}
}
