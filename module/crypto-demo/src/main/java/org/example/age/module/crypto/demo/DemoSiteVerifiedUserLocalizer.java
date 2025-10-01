package org.example.age.module.crypto.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.Localization;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;

/**
 * Implementation of {@link SiteVerifiedUserLocalizer}.
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoSiteVerifiedUserLocalizer implements SiteVerifiedUserLocalizer {

    private final SiteKeysConfig config;

    @Inject
    public DemoSiteVerifiedUserLocalizer(SiteKeysConfig config) {
        this.config = config;
    }

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user) {
        VerifiedUser localizedUser = Localization.localize(user, config.localization());
        return CompletableFuture.completedFuture(localizedUser);
    }
}
