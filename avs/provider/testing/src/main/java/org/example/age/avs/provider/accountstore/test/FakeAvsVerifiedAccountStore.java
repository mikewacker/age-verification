package org.example.age.avs.provider.accountstore.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.avs.spi.VerifiedAccount;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;

/** Fake, in-memory implementation of {@link AvsVerifiedAccountStore}. Uses seeded data. */
@Singleton
final class FakeAvsVerifiedAccountStore implements AvsVerifiedAccountStore {

    private final Map<String, VerifiedAccount> users = Map.of(
            "person",
            VerifiedAccount.builder()
                    .id("person")
                    .user(VerifiedUser.builder()
                            .pseudonym(SecureId.fromString("uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4"))
                            .ageRange(AgeRange.builder().min(40).max(40).build())
                            .build())
                    .build());

    @Inject
    public FakeAvsVerifiedAccountStore() {}

    @Override
    public CompletionStage<VerifiedAccount> load(String accountId) {
        return Optional.ofNullable(users.get(accountId))
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(new ForbiddenException()));
    }
}
