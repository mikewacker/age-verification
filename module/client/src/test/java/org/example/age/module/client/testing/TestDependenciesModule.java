package org.example.age.module.client.testing;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.client.SiteClientsConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link AvsClientsConfig}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 * <p>
 * Depends on an unbound {@link LazyPort}.
 */
@Module
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static SiteClientsConfig provideSiteClientsConfig(LazyPort port) {
        return TestConfig.createSite(port.get());
    }

    @Provides
    @Singleton
    static AvsClientsConfig provideAvsClientsConfig(LazyPort port) {
        return TestConfig.createAvs(port.get());
    }

    @Provides
    @Named("worker")
    @Singleton
    static ExecutorService providerWorker() {
        return Executors.newFixedThreadPool(1);
    }
}
