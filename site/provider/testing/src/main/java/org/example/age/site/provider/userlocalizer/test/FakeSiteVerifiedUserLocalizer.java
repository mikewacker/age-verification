package org.example.age.site.provider.userlocalizer.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.Localization;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/** Fake implementation of {@link SiteVerifiedUserLocalizer}. Uses a seeded key */
@Singleton
final class FakeSiteVerifiedUserLocalizer implements SiteVerifiedUserLocalizer {

    private final SecureId key = SecureId.fromString("NWKnDAiC7iM_hqothKM5Lnaor0xS77DzV9q9QpSeJLc");

    @Inject
    public FakeSiteVerifiedUserLocalizer() {}

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user) {
        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
