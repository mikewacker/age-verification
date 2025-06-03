package org.example.age.service.testing.crypto;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;

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
