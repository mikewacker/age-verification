package org.example.age.module.key.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link RefreshablePrivateSigningKeyProvider}</li>
 *     <li>{@link RefreshablePublicSigningKeyProvider}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}</li>
 * </ul>
 */
@Module
public interface TestKeyModule {

    @Binds
    RefreshablePrivateSigningKeyProvider bindRefreshablePrivateSigningKeyProvider(TestKeyProvider impl);

    @Binds
    RefreshablePublicSigningKeyProvider bindRefreshablePublicSigningKeyProvider(TestKeyProvider impl);

    @Binds
    RefreshablePseudonymKeyProvider bindRefreshablePseudonymKeyProvider(TestKeyProvider impl);
}
