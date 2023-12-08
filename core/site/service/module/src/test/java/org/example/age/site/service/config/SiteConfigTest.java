package org.example.age.site.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.api.JsonObjects;
import org.example.age.common.service.config.AvsLocation;
import org.junit.jupiter.api.Test;

public final class SiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        AvsLocation avsLocation =
                AvsLocation.builder("localhost", 80).redirectPath("/verify").build();
        SiteConfig siteConfig = SiteConfig.builder()
                .avsLocation(avsLocation)
                .id("Site")
                .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
                .build();
        byte[] rawSiteConfig = JsonObjects.serialize(siteConfig);
        SiteConfig rtSiteConfig = JsonObjects.deserialize(rawSiteConfig, new TypeReference<>() {});
        assertThat(rtSiteConfig).isEqualTo(siteConfig);
    }
}
