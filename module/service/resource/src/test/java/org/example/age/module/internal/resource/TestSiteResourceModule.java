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
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 */
@Module
public interface TestSiteResourceModule {

    @Provides
    @Named("resources")
    @Singleton
    static Class<?> provideResourcesClass() {
        return TestSiteResourceModule.class;
    }

    @Provides
    @Named("resources")
    @Singleton
    static Path provideResourcesSitePath() {
        return Path.of("Site");
    }
}
