package org.example.age.module.key.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.key.RefreshableKeyProvider;

/** Dagger module that publishes a binding for {@link RefreshableKeyProvider}. */
@Module
public interface TestKeyModule {

    @Binds
    RefreshableKeyProvider bindRefreshableKeyProvider(TestKeyProvider impl);
}
