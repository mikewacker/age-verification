package org.example.age.module.crypto.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.Localization;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;

/**
 * Implementation of {@link AvsVerifiedUserLocalizer}.
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final AvsKeysConfig config;

    @Inject
    public DemoAvsVerifiedUserLocalizer(AvsKeysConfig config) {
        this.config = config;
    }

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId) {
        SecureId key = config.localization().get(siteId);
        if (key == null) {
            return CompletableFuture.failedFuture(new NotFoundException());
        }

        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
