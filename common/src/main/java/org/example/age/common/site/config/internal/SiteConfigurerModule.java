package org.example.age.common.site.config.internal;

import com.google.common.net.HostAndPort;
import dagger.Module;
import dagger.Provides;
import java.security.PublicKey;
import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.site.config.SiteConfig;
import org.example.age.data.SecureId;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("avs") Supplier&lt;{@link HostAndPort}&gt;</code></li>
 *     <li><code>@Named("avsSigning") Supplier&lt;{@link PublicKey}&gt;</code></li>
 *     <li><code>@Named("siteId") Supplier&lt;String&gt;</code></li>
 *     <li><code>@Named("expiresIn") Supplier&lt;{@link Duration}&gt;</code></li>
 * </ul>
 *
 * <p>Depends on an unbound <code>Supplier&lt;{@link SiteConfig}&gt;</code>.</p>
 */
@Module
public interface SiteConfigurerModule {

    @Provides
    @Named("avs")
    @Singleton
    static Supplier<HostAndPort> provideAvsHostAndPort(Supplier<SiteConfig> siteConfigSupplier) {
        return () -> siteConfigSupplier.get().avsHostAndPort();
    }

    @Provides
    @Named("avsSigning")
    @Singleton
    static Supplier<PublicKey> provideAvsPublicKey(Supplier<SiteConfig> siteConfigSupplier) {
        return () -> siteConfigSupplier.get().avsPublicSigningKey();
    }

    @Provides
    @Named("siteId")
    @Singleton
    static Supplier<String> provideSiteId(Supplier<SiteConfig> siteConfigSupplier) {
        return () -> siteConfigSupplier.get().siteId();
    }

    @Provides
    @Named("pseudonymKey")
    @Singleton
    static Supplier<SecureId> providePseudonymKey(Supplier<SiteConfig> siteConfigSupplier) {
        return () -> siteConfigSupplier.get().pseudonymKey();
    }

    @Provides
    @Named("expiresIn")
    @Singleton
    static Supplier<Duration> provideExpiresIn(Supplier<SiteConfig> siteConfigSupplier) {
        return () -> siteConfigSupplier.get().expiresIn();
    }
}
