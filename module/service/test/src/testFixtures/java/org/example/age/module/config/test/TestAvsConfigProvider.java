package org.example.age.module.config.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.service.config.AvsConfig;
import org.example.age.service.config.RefreshableAvsConfigProvider;

@Singleton
final class TestAvsConfigProvider implements RefreshableAvsConfigProvider {

    private static final AvsConfig avsConfig = AvsConfig.builder()
            .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
            .redirectPath("/api/verification-request/link?request-id=%s")
            .build();

    @Inject
    public TestAvsConfigProvider() {}

    @Override
    public AvsConfig get() {
        return avsConfig;
    }
}
