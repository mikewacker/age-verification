package org.example.age.service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.testing.json.JsonTester;
import org.junit.jupiter.api.Test;

public final class SiteConfigTest {

    @Test
    public void serializeThenDeserialize() {
        SiteConfig siteConfig = SiteConfig.builder()
                .id("Site")
                .verifiedAccountExpiresInMinutes(Duration.ofDays(30).toMinutes())
                .redirectPath("/verify")
                .build();
        JsonTester.serializeThenDeserialize(siteConfig, new TypeReference<>() {});
    }
}
