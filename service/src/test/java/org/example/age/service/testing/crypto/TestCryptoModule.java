package org.example.age.service.testing.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link SiteVerifiedUserLocalizer}
 *     <li>{@link AvsVerifiedUserLocalizer}
 * </ul>
 * <p>
 * Depends on an unbound {@link ObjectMapper}.
 */
@Module
public interface TestCryptoModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(FakeAgeCertificateSignerVerifier impl);

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(FakeAgeCertificateSignerVerifier impl);

    @Binds
    SiteVerifiedUserLocalizer bindSiteVerifiedUserLocalizer(FakeSiteVerifiedUserLocalizer impl);

    @Binds
    AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(FakeAvsVerifiedUserLocalizer impl);
}
