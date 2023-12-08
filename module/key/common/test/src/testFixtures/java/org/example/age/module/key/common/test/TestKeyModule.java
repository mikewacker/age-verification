package org.example.age.module.key.common.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.key.common.RefreshableKeyProvider;

/** Dagger module that publishes a binding for {@link RefreshableKeyProvider}. */
@Module
public interface TestKeyModule {

    @Binds
    RefreshableKeyProvider bindRefreshableKeyProvider(TestKeyProvider impl);
}
