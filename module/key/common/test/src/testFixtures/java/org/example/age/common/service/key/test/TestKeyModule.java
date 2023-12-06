package org.example.age.common.service.key.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.key.RefreshableKeyProvider;

/** Dagger module that publishes a binding for {@link RefreshableKeyProvider}. */
@Module
public interface TestKeyModule {

    @Binds
    RefreshableKeyProvider bindRefreshableKeyProvider(TestKeyProvider impl);
}
