package org.example.age.module.config.resource;

import dagger.Binds;
import dagger.Module;
import java.nio.file.Path;
import org.example.age.module.internal.resource.ResourceLoaderModule;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link RefreshableAvsConfigProvider}, which loads config from {@code config/config.json}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}, which loads config from {@code config/siteConfigs.json}</li>
 * </ul>
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 *
 * <p>It loads resources before the server starts and is not refreshable.</p>
 */
@Module(includes = ResourceLoaderModule.class)
public interface ResourceAvsConfigModule {

    @Binds
    RefreshableAvsConfigProvider bindRefreshableAvsConfigProvider(ResourceAvsConfigProvider impl);

    @Binds
    RefreshableRegisteredSiteConfigProvider bindRefreshableRegisteredSiteConfigProvider(
            ResourceRegisteredSiteConfigProvider impl);
}
