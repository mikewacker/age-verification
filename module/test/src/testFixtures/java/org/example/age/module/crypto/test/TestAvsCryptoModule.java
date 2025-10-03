package org.example.age.module.crypto.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 * </ul>
 * <p>
 * It has two sites with IDs of "site1" and "site2".
 */
@Module
public interface TestAvsCryptoModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(FakeAgeCertificateSigner impl);

    @Binds
    AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(FakeAvsVerifiedUserLocalizer impl);
}
