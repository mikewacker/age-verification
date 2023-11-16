package org.example.age.common.site.auth.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.base.store.PendingStoreFactory;

/**
 * Dagger module that publishes a binding for {@link AuthManager}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link ObjectMapper}</li>
 * </ul>
 */
@Module
public interface AuthManagerModule {

    @Binds
    AuthManager bindAuthManager(AuthManagerImpl impl);
}
