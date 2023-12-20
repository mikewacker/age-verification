package org.example.age.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshableKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link AgeCertificateVerifier}</li>
 *     <li>{@link VerifiedUserLocalizer}</li>
 *     <li>{@link AuthMatchDataEncryptor}</li>
 * </ul>
 *
 * <p>Depends on an unbound {@link RefreshableKeyProvider}.</p>
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
