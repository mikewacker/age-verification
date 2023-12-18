package org.example.age.service.crypto.internal.common;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.common.RefreshableKeyProvider;

/**
 * Dagger module that publishes a binding for {@link AgeCertificateVerifier}.
 *
 * <p>Depends on an unbound {@link RefreshableKeyProvider}.</p>
 */
@Module
public interface AgeCertificateVerifierModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(AgeCertificateVerifierImpl impl);
}
