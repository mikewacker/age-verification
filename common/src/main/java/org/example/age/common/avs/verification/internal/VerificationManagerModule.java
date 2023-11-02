package org.example.age.common.avs.verification.internal;

import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import org.example.age.common.avs.store.RegisteredSiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.base.auth.AuthMatchDataExtractor;
import org.example.age.common.base.store.PendingStoreFactory;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link RegisteredSiteConfigStore}</li>
 *     <li>{@link VerifiedUserStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>@Named("expiresIn") Provider&lt;{@link Duration}&gt;</code></li>
 * </ul>
 */
@Module
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}
