package org.example.age.service.testing.store;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeRange;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.store.AvsVerifiedUserStore;

/** Fake, in-memory implementation of {@link AvsVerifiedUserStore}. Has one account with an ID of "person". */
@Singleton
final class FakeAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private final VerifiedUser user = VerifiedUser.builder()
            .pseudonym(SecureId.generate())
            .ageRange(AgeRange.builder().min(18).build())
            .build();

    @Inject
    public FakeAvsVerifiedUserStore() {}

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        Optional<VerifiedUser> maybeUser = accountId.equals("person") ? Optional.of(user) : Optional.empty();
        return CompletableFuture.completedFuture(maybeUser);
    }
}
