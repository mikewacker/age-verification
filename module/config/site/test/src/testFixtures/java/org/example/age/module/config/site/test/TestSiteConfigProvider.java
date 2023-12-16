package org.example.age.module.config.site.test;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.config.site.SiteConfig;
import org.example.age.service.module.config.site.RefreshableSiteConfigProvider;

@Singleton
final class TestSiteConfigProvider implements RefreshableSiteConfigProvider {

    private static final SiteConfig siteConfig = SiteConfig.builder()
            .id("Site")
            .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
            .build();

    @Inject
    public TestSiteConfigProvider() {}

    @Override
    public SiteConfig get() {
        return siteConfig;
    }
}
