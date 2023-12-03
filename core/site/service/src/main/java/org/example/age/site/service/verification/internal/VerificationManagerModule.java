package org.example.age.site.service.verification.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.security.PublicKey;
import java.time.Duration;
import org.example.age.common.service.crypto.internal.AgeCertificateVerifierModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.crypto.internal.VerifiedUserLocalizerModule;
import org.example.age.common.service.data.internal.ServiceObjectMapperModule;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.site.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>@Named("signing") {@link PublicKey}</code></li>
 *     <li><code>{@link PseudonymKeyProvider}</code></li>
 *     <li><code>@Named("siteId") String</code></li>
 *     <li><code>@Named("expiresIn") {@link Duration}</code></li>
 * </ul>
 *
 * <p>Also publishes a binding for <code>@Named("service") {@link ObjectMapper}</code>.</p>
 */
@Module(
        includes = {
            AgeCertificateVerifierModule.class,
            VerifiedUserLocalizerModule.class,
            AuthMatchDataEncryptorModule.class,
            ServiceObjectMapperModule.class,
        })
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}