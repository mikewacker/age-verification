package org.example.age.common.site.auth.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.auth.AuthMatchDataExtractor;

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
