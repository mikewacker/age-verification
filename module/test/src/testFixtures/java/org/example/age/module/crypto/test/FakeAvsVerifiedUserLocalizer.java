package org.example.age.module.crypto.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.Localization;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;

/** Fake implementation of {@link AvsVerifiedUserLocalizer}. It has two sites with IDs of "site1" and "site2". */
@Singleton
final class FakeAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final Map<String, SecureId> keys = Map.of("site1", SecureId.generate(), "site2", SecureId.generate());

    @Inject
    public FakeAvsVerifiedUserLocalizer() {}

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId) {
        SecureId key = keys.get(siteId);
        if (key == null) {
            return CompletableFuture.failedFuture(new NotFoundException());
        }

        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
