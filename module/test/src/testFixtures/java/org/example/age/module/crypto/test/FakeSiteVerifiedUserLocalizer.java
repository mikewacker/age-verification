package org.example.age.module.crypto.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.Localization;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;

/** Fake implementation of {@link SiteVerifiedUserLocalizer}. */
@Singleton
final class FakeSiteVerifiedUserLocalizer implements SiteVerifiedUserLocalizer {

    private final SecureId key = SecureId.generate();

    @Inject
    public FakeSiteVerifiedUserLocalizer() {}

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user) {
        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
