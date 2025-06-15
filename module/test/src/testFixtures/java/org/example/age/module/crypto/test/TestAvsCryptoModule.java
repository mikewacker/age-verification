package org.example.age.module.crypto.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 * </ul>
 */
@Module
public interface TestAvsCryptoModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(FakeAgeCertificateSigner impl);

    @Binds
    AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(FakeAvsVerifiedUserLocalizer impl);
}
