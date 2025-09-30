package org.example.age.module.store.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.testing.api.TestModels;

/** Fake, in-memory implementation of {@link AvsVerifiedUserStore}. It has one account with an ID of "person". */
@Singleton
final class FakeAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private final Map<String, VerifiedUser> users = Map.of("person", TestModels.createVerifiedUser());

    @Inject
    public FakeAvsVerifiedUserStore() {}

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        Optional<VerifiedUser> maybeUser = Optional.ofNullable(users.get(accountId));
        return CompletableFuture.completedFuture(maybeUser);
    }
}
