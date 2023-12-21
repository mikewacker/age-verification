package org.example.age.module.location.resource;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import java.nio.file.Path;
import javax.inject.Named;
import org.example.age.module.internal.resource.ResourceLoaderModule;
import org.example.age.service.location.RefreshableSiteLocationProvider;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteLocationProvider}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li>(optional) <code>@Named("resourcesLocation") {@link Path}</code></li>
 * </ul>
 */
@Module(includes = ResourceLoaderModule.class)
public interface ResourceSiteLocationModule {

    @Binds
    RefreshableSiteLocationProvider bindRefreshableSiteLocationProvider(ResourceLocationProvider impl);

    @BindsOptionalOf
    @Named("resourcesLocation")
    Path bindOptionalResourcesLocationPath();
}
