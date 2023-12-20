package org.example.age.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.crypto.internal.AgeCertificateVerifierModule;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.service.crypto.internal.VerifiedUserLocalizerModule;
import org.example.age.service.key.RefreshableKeyProvider;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link SiteVerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li>{@link RefreshableSiteConfigProvider}</li>
 * </ul>
 */
@Module(
        includes = {
            AgeCertificateVerifierModule.class,
            VerifiedUserLocalizerModule.class,
            AuthMatchDataEncryptorModule.class,
        })
public interface SiteVerificationManagerModule {

    @Binds
    SiteVerificationManager bindSiteVerificationManager(SiteVerificationManagerImpl impl);
}
