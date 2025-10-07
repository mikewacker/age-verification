package org.example.age.avs.provider.userlocalizer.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.Localization;
import org.example.age.common.api.crypto.SecureId;

/** Implementation of {@link AvsVerifiedUserLocalizer}. */
@Singleton
final class DemoAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final AvsLocalizationKeysConfig config;

    @Inject
    public DemoAvsVerifiedUserLocalizer(AvsLocalizationKeysConfig config) {
        this.config = config;
    }

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId) {
        SecureId key = config.keys().get(siteId);
        if (key == null) {
            return CompletableFuture.failedFuture(new NotFoundException());
        }

        VerifiedUser localizedUser = Localization.localize(user, key);
        return CompletableFuture.completedFuture(localizedUser);
    }
}
