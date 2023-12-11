package org.example.age.service.site.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.config.site.SiteConfig;
import org.example.age.module.key.common.RefreshableKeyProvider;
import org.example.age.module.store.common.PendingStoreFactory;
import org.example.age.module.store.common.VerificationStore;
import org.example.age.service.common.crypto.internal.AgeCertificateVerifierModule;
import org.example.age.service.common.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.service.common.crypto.internal.VerifiedUserLocalizerModule;

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
