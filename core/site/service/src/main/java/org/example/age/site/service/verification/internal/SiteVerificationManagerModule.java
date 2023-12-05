package org.example.age.site.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import java.security.PublicKey;
import org.example.age.api.JsonSerializer;
import org.example.age.common.service.crypto.internal.AgeCertificateVerifierModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.crypto.internal.VerifiedUserLocalizerModule;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.site.service.config.SiteConfig;
import org.example.age.site.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link SiteVerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>@Named("signing") Provider&lt;{@link PublicKey}&gt;</code></li>
 *     <li><code>{@link PseudonymKeyProvider}</code></li>
 *     <li><code>Provider&lt;{@link SiteConfig}&gt;</code></li>
 *     <li><code>@Named("service") {@link JsonSerializer}</code></li>
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