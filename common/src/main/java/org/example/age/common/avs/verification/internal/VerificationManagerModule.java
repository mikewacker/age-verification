package org.example.age.common.avs.verification.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.avs.store.RegisteredSiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.service.data.internal.AuthMatchDataEncryptorModule;

/**
 * Dagger module that publishes a binding for {@link VerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link RegisteredSiteConfigStore}</li>
 *     <li>{@link VerifiedUserStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link ObjectMapper}</li>
 *     <li><code>@Named("expiresIn") Provider&lt;{@link Duration}&gt;</code></li>
 * </ul>
 */
@Module(includes = AuthMatchDataEncryptorModule.class)
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}
