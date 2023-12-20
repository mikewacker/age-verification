package org.example.age.module.config.test.avs;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.config.avs.RefreshableAvsConfigProvider;
import org.example.age.service.config.avs.RefreshableRegisteredSiteConfigProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 * </ul>
 *
 * <p>It only provides configuration for a single registered site with ID {@code "Site"}.</p>
 *
 * <p>For testing purposes, the redirect path is an API redirect, not a UI redirect.</p>
 */
@Module
public interface TestAvsConfigModule {

    @Binds
    RefreshableAvsConfigProvider bindRefreshableAvsConfigProvider(TestAvsConfigProvider impl);

    @Binds
    RefreshableRegisteredSiteConfigProvider bindRefreshableRegisteredSiteConfigProvider(
            TestRegisteredSiteConfigProvider impl);
}
