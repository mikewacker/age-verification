package org.example.age.service.verification.internal.site;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.crypto.internal.common.AgeCertificateVerifierModule;
import org.example.age.service.crypto.internal.common.AuthMatchDataEncryptorModule;
import org.example.age.service.crypto.internal.common.VerifiedUserLocalizerModule;
import org.example.age.service.module.config.site.RefreshableSiteConfigProvider;
import org.example.age.service.module.key.common.RefreshableKeyProvider;
import org.example.age.service.store.common.PendingStoreFactory;
import org.example.age.service.store.common.VerificationStore;

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
