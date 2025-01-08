package org.example.age.module.client.testing;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.net.URI;
import java.net.URL;
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
        URL url = createLocalhostUrl(port.get());
        return SiteClientsConfig.builder().avsUrl(url).build();
    }

    @Provides
    @Singleton
    static AvsClientsConfig provideAvsClientsConfig(LazyPort port) {
        URL url = createLocalhostUrl(port.get());
        return AvsClientsConfig.builder().putSiteUrls("site", url).build();
    }

    @Provides
    @Named("worker")
    @Singleton
    static ExecutorService providerWorker() {
        return Executors.newFixedThreadPool(1);
    }

    /** Creates a URL for localhost from a port. */
    private static URL createLocalhostUrl(int port) {
        try {
            return new URI(String.format("http://localhost:%d", port)).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
