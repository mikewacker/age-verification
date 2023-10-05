package org.example.age.common.verification.auth;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that publishes a binding for {@link AuthManager}.
 *
 * <p>Depends on an unbound {@link AuthMatchDataExtractor}.</p>
 */
@Module
public interface AuthManagerModule {

    @Binds
    AuthManager bindAuthManager(AuthManagerImpl impl);
}
