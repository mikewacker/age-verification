package org.example.age.module.config.avs.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.config.avs.RefreshableAvsConfigProvider;
import org.example.age.service.module.config.avs.RefreshableRegisteredSiteConfigProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 * </ul>
 *
 * <p>It only provides configuration for a single registered site with ID {@code "Site"}.</p>
 */
@Module
public interface TestAvsConfigModule {

    @Binds
    RefreshableAvsConfigProvider bindRefreshableAvsConfigProvider(TestAvsConfigProvider impl);

    @Binds
    RefreshableRegisteredSiteConfigProvider bindRefreshableRegisteredSiteConfigProvider(
            TestRegisteredSiteConfigProvider impl);
}
