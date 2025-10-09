package org.example.age.module.crypto.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.Localization;
import org.example.age.common.api.crypto.SecureId;

/** Fake implementation of {@link AvsVerifiedUserLocalizer}. It has two sites with IDs of "site1" and "site2". */
@Singleton
final class FakeAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final Map<String, SecureId> keys = Map.of("site", SecureId.generate(), "other-site", SecureId.generate());

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
