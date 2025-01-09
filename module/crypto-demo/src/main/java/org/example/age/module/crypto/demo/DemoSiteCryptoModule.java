package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.service.api.crypto.AgeCertificateVerifier;
import org.example.age.service.api.crypto.SiteVerifiedUserLocalizer;

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
 *     <li>{@link ObjectMapper}
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
