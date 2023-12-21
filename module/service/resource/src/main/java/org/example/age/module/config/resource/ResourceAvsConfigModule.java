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
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 * </ul>
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resourcesAvs") {@link Path}</code></li>
 * </ul>
 */
@Module(includes = ResourceLoaderModule.class)
public interface ResourceAvsConfigModule {

    @Binds
    RefreshableAvsConfigProvider bindRefreshableAvsConfigProvider(ResourceAvsConfigProvider impl);

    @Binds
    RefreshableRegisteredSiteConfigProvider bindRefreshableRegisteredSiteConfigProvider(
            ResourceRegisteredSiteConfigProvider impl);
}
