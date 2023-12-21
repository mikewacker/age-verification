package org.example.age.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link AgeCertificateSigner}</li>
 *     <li>{@link VerifiedUserLocalizer}</li>
 *     <li>{@link AuthMatchDataEncryptor}</li>
 * </ul>
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link RefreshablePrivateSigningKeyProvider}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}</li>
 * </ul>
 */
@Module
public interface SignerCryptoModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(AgeCertificateSignerImpl impl);

    @Binds
    VerifiedUserLocalizer bindVerifiedUserLocalizer(VerifiedUserLocalizerImpl impl);

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
