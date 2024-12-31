package org.example.age.service.testing.crypto;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.util.Localization;

/** Fake implementation of {@link AvsVerifiedUserLocalizer}. */
@Singleton
final class FakeAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final Map<String, SecureId> keys = new HashMap<>();

    @Inject
    public FakeAvsVerifiedUserLocalizer() {}

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId) {
        SecureId key = keys.computeIfAbsent(siteId, id -> SecureId.generate());
        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
