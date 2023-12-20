package org.example.age.module.config.test;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.config.SiteConfig;

@Singleton
final class TestSiteConfigProvider implements RefreshableSiteConfigProvider {

    private static final SiteConfig siteConfig = SiteConfig.builder()
            .id("Site")
            .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
            .redirectPath("/api/verification-state")
            .build();

    @Inject
    public TestSiteConfigProvider() {}

    @Override
    public SiteConfig get() {
        return siteConfig;
    }
}
