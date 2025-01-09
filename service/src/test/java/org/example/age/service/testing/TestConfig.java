package org.example.age.service.testing;

import java.time.Duration;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.SiteServiceConfig;

/** Configuration for testing. */
public final class TestConfig {

    private static final SiteServiceConfig siteConfig = SiteServiceConfig.builder()
            .id("site")
            .verifiedAccountExpiresIn(Duration.ofDays(30))
            .build();
    private static final AvsServiceConfig avsConfig = AvsServiceConfig.builder()
            .verificationRequestExpiresIn(Duration.ofMinutes(5))
            .build();

    /** Gets the {@link SiteServiceConfig}. */
    public static SiteServiceConfig site() {
        return siteConfig;
    }

    /** Gets the {@link AvsServiceConfig}. */
    public static AvsServiceConfig avs() {
        return avsConfig;
    }

    // static class
    private TestConfig() {}
}
