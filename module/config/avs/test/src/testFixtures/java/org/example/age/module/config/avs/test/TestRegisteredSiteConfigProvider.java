package org.example.age.module.config.avs.test;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.user.AgeThresholds;
import org.example.age.service.config.avs.RegisteredSiteConfig;
import org.example.age.service.module.config.avs.RefreshableRegisteredSiteConfigProvider;

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
