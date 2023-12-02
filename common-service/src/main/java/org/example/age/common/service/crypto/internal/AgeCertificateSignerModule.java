package org.example.age.common.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import java.security.PrivateKey;

/**
 * Dagger module that publishes a binding for {@link AgeCertificateSigner}.
 *
 * <p>Depends on an unbound <code>@Named("signing") Provider&lt;{@link PrivateKey}&gt;</code>,
 * which must be an <code>Ed25519</code> key.</p>
 */
@Module
public interface AgeCertificateSignerModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(AgeCertificateSignerImpl impl);
}
