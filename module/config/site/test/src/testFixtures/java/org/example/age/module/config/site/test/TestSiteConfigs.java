package org.example.age.module.config.site.test;

import java.time.Duration;
import org.example.age.module.config.common.AvsLocation;
import org.example.age.module.config.site.SiteConfig;

/** Test {@link SiteConfig}'s. */
final class TestSiteConfigs {

    /** Creates a {@link SiteConfig} from an {@link AvsLocation}. */
    public static SiteConfig create(AvsLocation avsLocation) {
        return SiteConfig.builder()
                .avsLocation(avsLocation)
                .id("Site")
                .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
                .build();
    }

    // static class
    private TestSiteConfigs() {}
}
