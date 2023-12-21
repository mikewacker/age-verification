package org.example.age.module.config.resource;

import dagger.Binds;
import dagger.Module;
import java.nio.file.Path;
import org.example.age.service.config.RefreshableSiteConfigProvider;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteConfigProvider}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resourcesSite") {@link Path}</code></li>
 * </ul>
 */
@Module
public interface ResourceSiteConfigModule {

    @Binds
    RefreshableSiteConfigProvider bindRefreshableSiteConfigProvider(ResourceSiteConfigProvider impl);
}
