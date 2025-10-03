package org.example.age.module.crypto.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 * </ul>
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 * <p>
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Module(includes = BaseEnvModule.class)
public interface DemoAvsCryptoModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(DemoAgeCertificateSigner impl);

    @Binds
    AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(DemoAvsVerifiedUserLocalizer impl);
}
