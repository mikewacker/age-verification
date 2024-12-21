package org.example.age.demo.server;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 *
 * <p>Depends on an unbound {@code @Named("name") String}.</p>
 */
@Module
interface DemoResourceModule {

    @Provides
    @Named("resources")
    @Singleton
    static Class<?> provideResourcesClass() {
        return DemoResourceModule.class;
    }

    @Provides
    @Named("resources")
    @Singleton
    static Path provideResourcesPath(@Named("name") String name) {
        return Path.of(name);
    }
}
