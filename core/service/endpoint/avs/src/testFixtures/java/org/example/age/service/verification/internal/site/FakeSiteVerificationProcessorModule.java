package org.example.age.service.verification.internal.site;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.crypto.internal.common.AgeCertificateVerifierModule;
import org.example.age.service.module.key.common.RefreshableKeyProvider;
import org.example.age.service.store.common.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link FakeSiteVerificationProcessor}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 * </ul>
 */
@Module(includes = AgeCertificateVerifierModule.class)
public interface FakeSiteVerificationProcessorModule {

    @Binds
    FakeSiteVerificationProcessor bindFakeSiteVerificationProcessor(FakeSiteVerificationProcessorImpl impl);
}
