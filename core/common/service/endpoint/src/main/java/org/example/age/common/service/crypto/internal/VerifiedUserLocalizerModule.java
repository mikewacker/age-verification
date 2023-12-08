package org.example.age.common.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.key.common.RefreshableKeyProvider;

/**
 * Dagger module that publishes a binding for {@link VerifiedUserLocalizer}.
 *
 * <p>Depends on an unbound {@link RefreshableKeyProvider}.</p>
 */
@Module
public interface VerifiedUserLocalizerModule {

    @Binds
    VerifiedUserLocalizer bindVerifiedUserLocaliazer(VerifiedUserLocalizerImpl impl);
}
