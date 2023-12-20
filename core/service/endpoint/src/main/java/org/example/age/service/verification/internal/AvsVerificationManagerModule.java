package org.example.age.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.service.crypto.internal.VerifiedUserLocalizerModule;
import org.example.age.service.key.RefreshableKeyProvider;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link AvsVerificationManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 * </ul>
 */
@Module(
        includes = {
            AgeCertificateSignerModule.class,
            VerifiedUserLocalizerModule.class,
            AuthMatchDataEncryptorModule.class,
        })
public interface AvsVerificationManagerModule {

    @Binds
    AvsVerificationManager bindAvsVerificationManager(AvsVerificationManagerImpl impl);
}
