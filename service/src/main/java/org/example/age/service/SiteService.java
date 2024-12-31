package org.example.age.service;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AuthMatchData;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.AvsApi;
import org.example.age.service.api.crypto.AgeCertificateVerifier;
import org.example.age.service.api.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.api.request.AccountIdExtractor;
import org.example.age.service.api.store.PendingStore;
import org.example.age.service.api.store.PendingStoreRepository;
import org.example.age.service.api.store.SiteVerificationStore;
import org.example.age.service.util.AsyncCalls;
import retrofit2.Call;

/** Service implementation of {@link SiteApi}. */
@Singleton
final class SiteService implements SiteApi {

    private final AccountIdExtractor accountIdExtractor;
    private final AvsApi avsClient;
    private final SiteVerificationStore verificationStore;
    private final PendingStore<String> pendingRequestStore;
    private final AgeCertificateVerifier ageCertificateVerifier;
    private final SiteVerifiedUserLocalizer userLocalizer;
    private final SiteServiceConfig config;

    @Inject
    public SiteService(
            AccountIdExtractor accountIdExtractor,
            @Named("client") AvsApi avsClient,
            SiteVerificationStore verificationStore,
            PendingStoreRepository pendingStoreRepository,
            AgeCertificateVerifier ageCertificateVerifier,
            SiteVerifiedUserLocalizer userLocalizer,
            SiteServiceConfig config) {
        this.accountIdExtractor = accountIdExtractor;
        this.avsClient = avsClient;
        this.verificationStore = verificationStore;
        this.pendingRequestStore = pendingStoreRepository.get("request", String.class);
        this.ageCertificateVerifier = ageCertificateVerifier;
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        String accountId = accountIdExtractor.getForRequest();
        return verificationStore.load(accountId);
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        String accountId = accountIdExtractor.getForRequest();
        AuthMatchData authMatchData = AuthMatchData.builder().name("").data("").build(); // not implemented
        Call<VerificationRequest> requestCall = avsClient.createVerificationRequestForSite(config.id(), authMatchData);
        return AsyncCalls.make(requestCall).thenCompose(request -> linkRequestToAccount(request, accountId));
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        CompletionStage<AgeCertificate> ageCertificateStage = validateSignedAgeCertificate(signedAgeCertificate);
        CompletionStage<String> accountStage =
                ageCertificateStage.thenApply(AgeCertificate::getRequest).thenCompose(this::findAccountToVerify);
        CompletionStage<VerifiedUser> localizedUserStage =
                ageCertificateStage.thenApply(AgeCertificate::getUser).thenCompose(userLocalizer::localize);
        return accountStage
                .thenCombine(localizedUserStage, this::tryVerifyAccount)
                .thenCompose(Function.identity());
    }

    /** Links a {@link VerificationRequest} to an account before returning the request. */
    private CompletionStage<VerificationRequest> linkRequestToAccount(VerificationRequest request, String accountId) {
        return pendingRequestStore
                .put(request.getId().toString(), accountId, request.getExpiration())
                .thenApply(v -> request);
    }

    /** Validates a {@link SignedAgeCertificate}. */
    private CompletionStage<AgeCertificate> validateSignedAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        OffsetDateTime expiration =
                signedAgeCertificate.getAgeCertificate().getRequest().getExpiration();
        if (expiration.isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            return CompletableFuture.failedFuture(new ClientErrorException(410));
        }

        return ageCertificateVerifier.verify(signedAgeCertificate);
    }

    /** Finds the account to verify for the {@link VerificationRequest}. */
    private CompletionStage<String> findAccountToVerify(VerificationRequest request) {
        return pendingRequestStore
                .tryRemove(request.getId().toString())
                .thenApply(maybeAccountId -> maybeAccountId.orElseThrow(NotFoundException::new));
    }

    /** Tries to verify an account with a localized {@link VerifiedUser}. */
    private CompletionStage<Void> tryVerifyAccount(String accountId, VerifiedUser localizedUser) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(config.verifiedAccountExpiresIn());
        VerificationState state = VerificationState.builder()
                .status(VerificationStatus.VERIFIED)
                .user(localizedUser)
                .expiration(expiration)
                .build();
        return verificationStore.trySave(accountId, state).thenAccept(maybeDuplicateAccountId -> {
            if (maybeDuplicateAccountId.isPresent()) {
                throw new ClientErrorException(409);
            }
        });
    }
}
