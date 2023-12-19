package org.example.age.module.config.test.avs;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.config.avs.AvsConfig;
import org.example.age.service.config.avs.RefreshableAvsConfigProvider;

@Singleton
final class TestAvsConfigProvider implements RefreshableAvsConfigProvider {

    private static final AvsConfig avsConfig = AvsConfig.builder()
            .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
            .redirectPath("/api/linked-verification-request?request-id=%s")
            .build();

    @Inject
    public TestAvsConfigProvider() {}

    @Override
    public AvsConfig get() {
        return avsConfig;
    }
}
