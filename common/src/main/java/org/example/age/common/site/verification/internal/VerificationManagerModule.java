package org.example.age.common.site.verification.internal;

import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.site.store.VerificationStore;
import org.example.age.data.SecureId;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>@Named("pseudonymKey") Provider&lt;{@link SecureId}&gt;</code></li>
 *     <li><code>@Named("expiresIn") Provider&lt;{@link Duration}&gt;</code>: expiration for verified accounts</li>
 * </ul>
 */
@Module
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}
