package org.example.age.module.internal.resource;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that publishes a binding for {@link ResourceLoader}.
 *
 * <p>Depends on an unbound <code>@Named("resources") {@link Class}&lt;?&gt;</code>.</p>
 */
@Module
public interface ResourceLoaderModule {

    @Binds
    ResourceLoader bindResourceLoader(ResourceLoaderImpl impl);
}
