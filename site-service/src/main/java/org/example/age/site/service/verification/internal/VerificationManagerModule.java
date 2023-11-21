package org.example.age.site.service.verification.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.security.PublicKey;
import java.time.Duration;
import org.example.age.common.service.data.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.data.crypto.SecureId;
import org.example.age.site.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link ObjectMapper}</li>
 *     <li><code>@Named("avsSigning") {@link PublicKey}</code></li>
 *     <li><code>@Named("siteId") String</code></li>
 *     <li><code>@Named("pseudonymKey") {@link SecureId}</code></li>
 *     <li><code>@Named("expiresIn") {@link Duration}</code></li>
 * </ul>
 */
@Module(includes = AuthMatchDataEncryptorModule.class)
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}
