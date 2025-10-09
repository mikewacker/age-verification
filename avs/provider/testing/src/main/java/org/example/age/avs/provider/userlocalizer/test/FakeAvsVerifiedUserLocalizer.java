package org.example.age.avs.provider.userlocalizer.test;

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

/** Fake implementation of {@link AvsVerifiedUserLocalizer}. Uses seeded keys. */
@Singleton
final class FakeAvsVerifiedUserLocalizer implements AvsVerifiedUserLocalizer {

    private final Map<String, SecureId> keys = Map.of(
            "site",
            SecureId.fromString("pER-dDPdsvdvcP9szpckd6GHHc1qg44Rt70LTUqHTpY"),
            "other-site",
            SecureId.fromString("W1zah29NMWEOEsd8VNFX6E3Vo8Z-HLNQ5cDH3-9KyVg"));

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
