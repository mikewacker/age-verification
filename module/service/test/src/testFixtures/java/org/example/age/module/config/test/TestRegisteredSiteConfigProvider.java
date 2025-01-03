package org.example.age.module.config.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.data.user.AgeThresholds;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.config.RegisteredSiteConfig;

@Singleton
final class TestRegisteredSiteConfigProvider implements RefreshableRegisteredSiteConfigProvider {

    private static final RegisteredSiteConfig siteConfig = RegisteredSiteConfig.builder("Site")
            .ageThresholds(AgeThresholds.of(13, 18))
            .build();

    @Inject
    public TestRegisteredSiteConfigProvider() {}

    @Override
    public Optional<RegisteredSiteConfig> tryGet(String siteId) {
        return siteId.equals("Site") ? Optional.of(siteConfig) : Optional.empty();
    }
}
