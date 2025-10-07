package org.example.age.avs.provider.userlocalizer.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.Localization;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/** Implementation of {@link SiteVerifiedUserLocalizer}. */
@Singleton
final class DemoSiteVerifiedUserLocalizer implements SiteVerifiedUserLocalizer {

    private final SiteLocalizationKeyConfig config;

    @Inject
    public DemoSiteVerifiedUserLocalizer(SiteLocalizationKeyConfig config) {
        this.config = config;
    }

    @Override
    public CompletionStage<VerifiedUser> localize(VerifiedUser user) {
        VerifiedUser localizedUser = Localization.localize(user, config.key());
        return CompletableFuture.completedFuture(localizedUser);
    }
}
