package org.example.age.common.site.auth.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.auth.AuthMatchDataExtractor;
import org.example.age.common.store.PendingStoreFactory;

/**
 * Dagger module that publishes a binding for {@link AuthManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link PendingStoreFactory}</li>
 * </ul>
 */
@Module
public interface AuthManagerModule {

    @Binds
    AuthManager bindAuthManager(AuthManagerImpl impl);
}
