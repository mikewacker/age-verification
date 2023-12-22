package org.example.age.module.config.resource;

import dagger.Binds;
import dagger.Module;
import java.nio.file.Path;
import org.example.age.module.internal.resource.ResourceLoaderModule;
import org.example.age.service.config.RefreshableSiteConfigProvider;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteConfigProvider},
 * which loads config from {@code config/config.json}.
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
public interface ResourceSiteConfigModule {

    @Binds
    RefreshableSiteConfigProvider bindRefreshableSiteConfigProvider(ResourceSiteConfigProvider impl);
}
