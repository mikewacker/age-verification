package org.example.age.common.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import java.security.PublicKey;

/**
 * Dagger module that publishes a binding for {@link AgeCertificateVerifier}.
 *
 * <p>Depends on an unbound <code>@Named("signing") Provider&lt;{@link PublicKey}&gt;</code>,
 * which must be an <code>Ed25519</code> key.</p>
 */
@Module
public interface AgeCertificateVerifierModule {

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(AgeCertificateVerifierImpl impl);
}
