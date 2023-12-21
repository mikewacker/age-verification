package org.example.age.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.crypto.internal.SignerCryptoModule;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link AvsVerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshablePrivateSigningKeyProvider}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 * </ul>
 */
@Module(includes = SignerCryptoModule.class)
public interface AvsVerificationManagerModule {

    @Binds
    AvsVerificationManager bindAvsVerificationManager(AvsVerificationManagerImpl impl);
}
