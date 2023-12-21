package org.example.age.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.crypto.internal.VerifierCryptoModule;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link FakeSiteVerificationProcessor}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link RefreshablePublicSigningKeyProvider}</li>
 * </ul>
 */
@Module(includes = VerifierCryptoModule.class)
public interface FakeSiteVerificationProcessorModule {

    @Binds
    FakeSiteVerificationProcessor bindFakeSiteVerificationProcessor(FakeSiteVerificationProcessorImpl impl);
}
