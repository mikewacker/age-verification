package org.example.age.module.config.site.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.config.site.RefreshableSiteConfigProvider;

/** Dagger module that publishes a binding for {@link RefreshableSiteConfigProvider}. */
@Module
public interface TestSiteConfigModule {

    @Binds
    RefreshableSiteConfigProvider bindRefreshableSiteConfigProvider(TestSiteConfigProvider impl);
}
