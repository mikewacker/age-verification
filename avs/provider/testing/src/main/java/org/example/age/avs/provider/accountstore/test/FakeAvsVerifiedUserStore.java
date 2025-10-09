package org.example.age.avs.provider.accountstore.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;

/** Fake, in-memory implementation of {@link AvsVerifiedUserStore}. Uses seeded data. */
@Singleton
final class FakeAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private final Map<String, VerifiedUser> users = Map.of(
            "person",
            VerifiedUser.builder()
                    .pseudonym(SecureId.fromString("uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4"))
                    .ageRange(AgeRange.builder().min(40).max(40).build())
                    .build());

    @Inject
    public FakeAvsVerifiedUserStore() {}

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        Optional<VerifiedUser> maybeUser = Optional.ofNullable(users.get(accountId));
        return CompletableFuture.completedFuture(maybeUser);
    }
}
