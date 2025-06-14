package org.example.age.module.crypto.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteVerifiedUserLocalizer}
 * </ul>
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link SiteKeysConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 * <p>
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Module
public interface DemoSiteCryptoModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(DemoAgeCertificateVerifier impl);

    @Binds
    SiteVerifiedUserLocalizer bindSiteVerifiedUserLocalized(DemoSiteVerifiedUserLocalizer impl);
}
