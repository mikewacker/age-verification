package org.example.age.module.internal.resource;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resourcesSite") {@link Path}</code></li>
 *     <li><code>@Named("resourcesAvs") {@link Path}</code></li>
 * </ul>
 */
@Module(includes = ResourceLoaderModule.class)
public interface TestResourceModule {

    @Provides
    @Named("resources")
    @Singleton
    static Class<?> provideResourcesClass() {
        return TestResourceModule.class;
    }

    @Provides
    @Named("resourcesSite")
    @Singleton
    static Path provideResourcesSitePath() {
        return Path.of("Site");
    }

    @Provides
    @Named("resourcesAvs")
    @Singleton
    static Path provideResourcesAvsPath() {
        return Path.of("AVS");
    }
}
