package org.example.age.site.service.config.internal;

import dagger.Module;
import dagger.Provides;
import java.security.PublicKey;
import java.time.Duration;
import javax.inject.Named;
import org.example.age.common.service.data.AvsLocation;
import org.example.age.data.crypto.SecureId;
import org.example.age.site.service.config.SiteConfig;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("bridged") {@link AvsLocation}</code></li>
 *     <li><code>@Named("bridgedSigning") {@link PublicKey}</code></li>
 *     <li><code>@Named("siteId") String</code></li>
 *     <li><code>@Named("pseudonymKey") {@link SecureId}</code></li>
 *     <li><code>@Named("expiresIn") {@link Duration}</code></li>
 * </ul>
 *
 * <p>Depends on an unbound {@link SiteConfig}</code>.</p>
 */
@Module
public interface SiteConfigurerModule {

    @Provides
    @Named("bridged")
    static AvsLocation provideAvsLocation(SiteConfig siteConfig) {
        return siteConfig.avsLocation();
    }

    @Provides
    @Named("bridgedSigning")
    static PublicKey provideAvsPublicSigningKey(SiteConfig siteConfig) {
        return siteConfig.avsPublicSigningKey();
    }

    @Provides
    @Named("siteId")
    static String provideSiteId(SiteConfig siteConfig) {
        return siteConfig.siteId();
    }

    @Provides
    @Named("pseudonymKey")
    static SecureId providePseudonymKey(SiteConfig siteConfig) {
        return siteConfig.pseudonymKey();
    }

    @Provides
    @Named("expiresIn")
    static Duration provideExpiresIn(SiteConfig siteConfig) {
        return siteConfig.expiresIn();
    }
}
