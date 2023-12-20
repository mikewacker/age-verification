package org.example.age.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class SiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        SiteConfig siteConfig = SiteConfig.builder()
                .id("Site")
                .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
                .redirectPath("/verify")
                .build();
        byte[] rawSiteConfig = JsonValues.serialize(siteConfig);
        SiteConfig rtSiteConfig = JsonValues.deserialize(rawSiteConfig, new TypeReference<>() {});
        assertThat(rtSiteConfig).isEqualTo(siteConfig);
    }
}
