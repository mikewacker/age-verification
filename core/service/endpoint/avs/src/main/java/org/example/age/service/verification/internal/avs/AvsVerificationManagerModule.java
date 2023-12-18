package org.example.age.service.verification.internal.avs;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.avs.RefreshableAvsConfigProvider;
import org.example.age.service.config.avs.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.crypto.internal.common.AgeCertificateSignerModule;
import org.example.age.service.crypto.internal.common.AuthMatchDataEncryptorModule;
import org.example.age.service.crypto.internal.common.VerifiedUserLocalizerModule;
import org.example.age.service.key.common.RefreshableKeyProvider;
import org.example.age.service.store.common.PendingStoreFactory;
import org.example.age.service.store.common.VerificationStore;

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
