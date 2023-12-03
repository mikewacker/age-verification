package org.example.age.site.service.config.test;

import java.time.Duration;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.site.service.config.SiteConfig;

/** Test {@link SiteConfig}'s. */
final class TestSiteConfigs {

    /** Creates a {@link SiteConfig} from an {@link AvsLocation}. */
    public static SiteConfig create(AvsLocation avsLocation) {
        return SiteConfig.builder()
                .avsLocation(avsLocation)
                .siteId("Site")
                .expiresIn(Duration.ofDays(30))
                .build();
    }

    // static class
    private TestSiteConfigs() {}
}
