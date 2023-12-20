package org.example.age.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshableKeyProvider;

/**
 * Dagger module that publishes a binding for {@link AgeCertificateSigner}.
 *
 * <p>Depends on an unbound {@link RefreshableKeyProvider}.</p>
 */
@Module
public interface AgeCertificateSignerModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(AgeCertificateSignerImpl impl);
}
