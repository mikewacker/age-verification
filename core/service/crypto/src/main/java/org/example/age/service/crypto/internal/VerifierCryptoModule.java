package org.example.age.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link AgeCertificateVerifier}</li>
 *     <li>{@link VerifiedUserLocalizer}</li>
 *     <li>{@link AuthMatchDataEncryptor}</li>
 * </ul>
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link RefreshablePublicSigningKeyProvider}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}</li>
 * </ul>
 */
@Module
public interface VerifierCryptoModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(AgeCertificateVerifierImpl impl);

    @Binds
    VerifiedUserLocalizer bindVerifiedUserLocalizer(VerifiedUserLocalizerImpl impl);

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
