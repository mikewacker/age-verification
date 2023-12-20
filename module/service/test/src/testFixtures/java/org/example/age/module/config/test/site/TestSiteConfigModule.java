package org.example.age.module.config.test.site;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.site.RefreshableSiteConfigProvider;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteConfigProvider}.
 *
 * <p>For testing purposes, the redirect path is an API redirect, not a UI redirect.</p>
 */
@Module
public interface TestSiteConfigModule {

    @Binds
    RefreshableSiteConfigProvider bindRefreshableSiteConfigProvider(TestSiteConfigProvider impl);
}
