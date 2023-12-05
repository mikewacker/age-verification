package org.example.age.site.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.example.age.common.service.config.AvsLocation;
import org.junit.jupiter.api.Test;

public final class SiteConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AvsLocation avsLocation =
                AvsLocation.builder("localhost", 80).redirectPath("/verify").build();
        SiteConfig siteConfig = SiteConfig.builder()
                .avsLocation(avsLocation)
                .id("Site")
                .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
                .build();
        byte[] rawSiteConfig = mapper.writeValueAsBytes(siteConfig);
        SiteConfig rtSiteConfig = mapper.readValue(rawSiteConfig, new TypeReference<>() {});
        assertThat(rtSiteConfig).isEqualTo(siteConfig);
    }
}
