package org.example.age.site.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.crypto.internal.AgeCertificateVerifierModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.crypto.internal.VerifiedUserLocalizerModule;
import org.example.age.common.service.key.RefreshableKeyProvider;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.common.service.store.VerificationStore;
import org.example.age.site.service.config.SiteConfig;

/**
 * Dagger module that publishes a binding for {@link SiteVerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li><code>Provider&lt;{@link SiteConfig}&gt;</code></li>
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
