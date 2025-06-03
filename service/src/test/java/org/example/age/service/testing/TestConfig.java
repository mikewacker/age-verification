package org.example.age.service.testing;

import java.time.Duration;
import java.util.Map;
import org.example.age.api.AgeThresholds;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.SiteServiceConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final SiteServiceConfig siteService = SiteServiceConfig.builder()
            .id("site1")
            .verifiedAccountExpiresIn(Duration.ofDays(30))
            .build();
    private static final AvsServiceConfig avsService = AvsServiceConfig.builder()
            .verificationRequestExpiresIn(Duration.ofMinutes(5))
            .ageThresholds(Map.of("site1", AgeThresholds.of(18), "site2", AgeThresholds.of(18)))
            .build();

    /** Gets the configuration for the service on the site. */
    public static SiteServiceConfig siteService() {
        return siteService;
    }

    /** Gets the configuration for the service on the age verification service. */
    public static AvsServiceConfig avsService() {
        return avsService;
    }

    private TestConfig() {} // static class
}
