package org.example.age.module.crypto.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.service.api.crypto.Localization;
import org.example.age.service.api.crypto.SiteVerifiedUserLocalizer;

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
