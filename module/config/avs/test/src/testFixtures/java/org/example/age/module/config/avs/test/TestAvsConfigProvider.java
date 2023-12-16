package org.example.age.module.config.avs.test;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.config.avs.AvsConfig;
import org.example.age.service.module.config.RefreshableAvsConfigProvider;

@Singleton
final class TestAvsConfigProvider implements RefreshableAvsConfigProvider {

    private static final AvsConfig avsConfig = AvsConfig.builder()
            .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
            .build();

    @Inject
    public TestAvsConfigProvider() {}

    @Override
    public AvsConfig get() {
        return avsConfig;
    }
}