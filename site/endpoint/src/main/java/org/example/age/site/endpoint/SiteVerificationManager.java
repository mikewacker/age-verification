package org.example.age.site.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.spi.PendingStore;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.site.api.VerificationState;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/** Manages the age verification protocol. */
@Singleton
final class SiteVerificationManager {

    private final SiteVerifiedAccountStore accountStore;
    private final PendingStore<String> pendingRequestStore;
    private final SiteVerifiedUserLocalizer userLocalizer;
    private final SiteEndpointConfig config;

    @Inject
    public SiteVerificationManager(
            SiteVerifiedAccountStore accountStore,
            PendingStoreRepository pendingStores,
            SiteVerifiedUserLocalizer userLocalizer,
            SiteEndpointConfig config) {
        this.accountStore = accountStore;
        this.pendingRequestStore = pendingStores.get("request", String.class);
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    /** Gets the verification state for the account. */
    public CompletionStage<VerificationState> getVerificationState(String accountId) {
        return accountStore.load(accountId);
    }

    /** Handles a verification request from the age verification service. */
    public CompletionStage<VerificationRequest> onVerificationRequestReceived(
            String accountId, VerificationRequest request) {
        return pendingRequestStore
                .put(request.getId().toString(), accountId, request.getExpiration())
                .thenApply(__ -> request);
    }

    /** Handles an age certificate from the age verification service. */
    public CompletionStage<Void> onAgeCertificateReceived(AgeCertificate ageCertificate) {
        CompletionStage<String> accountStage = getAccount(ageCertificate.getRequest());
        CompletionStage<VerifiedUser> localizedUserStage = userLocalizer.localize(ageCertificate.getUser());
        return accountStage.thenCombine(localizedUserStage, this::verifyAccount).thenCompose(Function.identity());
    }

    /** Gets the account linked to the verification request. */
    private CompletionStage<String> getAccount(VerificationRequest request) {
        return pendingRequestStore
                .tryRemove(request.getId().toString())
                .thenApply(maybeAccountId -> maybeAccountId.orElseThrow(NotFoundException::new));
    }

    /** Verifies the account, unless a duplicate verification occurs. */
    private CompletionStage<Void> verifyAccount(String accountId, VerifiedUser localizedUser) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(config.verifiedAccountExpiresIn());
        return accountStore
                .trySave(accountId, localizedUser, expiration)
                .thenAccept(maybeDuplicateAccountId -> maybeDuplicateAccountId.ifPresent(a -> {
                    throw new ClientErrorException(409);
                }));
    }
}
