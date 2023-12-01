package org.example.age.common.avs.verification.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import org.example.age.common.api.extractor.AuthMatchDataExtractor;
import org.example.age.common.avs.store.RegisteredSiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.store.PendingStoreFactory;

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
