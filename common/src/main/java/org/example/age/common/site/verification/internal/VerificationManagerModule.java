package org.example.age.common.site.verification.internal;

import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import org.example.age.common.site.verification.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li><code>@Named("expiresIn") Supplier&lt;{@link Duration}&gt;</code>: expiration for verified accounts</li>
 * </ul>
 */
@Module
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}