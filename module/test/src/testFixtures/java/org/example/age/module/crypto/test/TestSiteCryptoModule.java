package org.example.age.module.crypto.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteVerifiedUserLocalizer}
 * </ul>
 */
@Module
public interface TestSiteCryptoModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(FakeAgeCertificateVerifier impl);

    @Binds
    SiteVerifiedUserLocalizer bindSiteVerifiedUserLocalizer(FakeSiteVerifiedUserLocalizer impl);
}
