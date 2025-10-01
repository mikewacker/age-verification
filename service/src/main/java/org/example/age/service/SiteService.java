package org.example.age.service;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.AvsApi;
import org.example.age.common.util.AsyncCalls;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.module.request.AccountIdContext;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.SiteVerificationStore;
import retrofit2.Call;

/** Service implementation of {@link SiteApi}. */
@Singleton
final class SiteService implements SiteApi {

    private final AccountIdContext accountIdContext;
    private final AvsApi avsClient;
    private final SiteVerificationStore verificationStore;
    private final PendingStore<String> pendingRequestStore;
    private final AgeCertificateVerifier ageCertificateVerifier;
    private final SiteVerifiedUserLocalizer userLocalizer;
    private final SiteServiceConfig config;

    @Inject
    public SiteService(
            AccountIdContext accountIdContext,
            @Named("client") AvsApi avsClient,
            SiteVerificationStore verificationStore,
            PendingStoreRepository pendingStores,
            AgeCertificateVerifier ageCertificateVerifier,
            SiteVerifiedUserLocalizer userLocalizer,
            SiteServiceConfig config) {
        this.accountIdContext = accountIdContext;
        this.avsClient = avsClient;
        this.verificationStore = verificationStore;
        this.pendingRequestStore = pendingStores.get("request", String.class);
        this.ageCertificateVerifier = ageCertificateVerifier;
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        String accountId = accountIdContext.getForRequest();
        return verificationStore.load(accountId);
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        String accountId = accountIdContext.getForRequest();
        return createVerificationRequestForThisSite()
                .thenCompose(request -> linkVerificationRequestToAccount(request, accountId));
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        CompletionStage<AgeCertificate> ageCertificateStage = validateSignedAgeCertificate(signedAgeCertificate);
        CompletionStage<String> accountStage = ageCertificateStage
                .thenApply(AgeCertificate::getRequest)
                .thenCompose(this::findAccountForVerificationRequest);
        CompletionStage<VerifiedUser> localizedUserStage =
                ageCertificateStage.thenApply(AgeCertificate::getUser).thenCompose(userLocalizer::localize);
        return accountStage.thenCombine(localizedUserStage, this::verifyAccount).thenCompose(Function.identity());
    }

    /** Creates a {@link VerificationRequest} for this site. */
    private CompletionStage<VerificationRequest> createVerificationRequestForThisSite() {
        Call<VerificationRequest> call = avsClient.createVerificationRequestForSite(config.id());
        return AsyncCalls.make(call)
                .exceptionallyCompose(t -> CompletableFuture.failedFuture(new InternalServerErrorException(t)));
    }

    /** Links the {@link VerificationRequest} to the account. */
    private CompletionStage<VerificationRequest> linkVerificationRequestToAccount(
            VerificationRequest request, String accountId) {
        return pendingRequestStore
                .put(request.getId().toString(), accountId, request.getExpiration())
                .thenApply(v -> request);
    }

    /** Validates a {@link SignedAgeCertificate}. */
    private CompletionStage<AgeCertificate> validateSignedAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        VerificationRequest request = signedAgeCertificate.getAgeCertificate().getRequest();
        if (!request.getSiteId().equals(config.id())) {
            return CompletableFuture.failedFuture(new ForbiddenException());
        }

        if (request.getExpiration().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            return CompletableFuture.failedFuture(new NotFoundException());
        }

        return ageCertificateVerifier.verify(signedAgeCertificate);
    }

    /** Finds the account that is linked to the {@link VerificationRequest}. */
    private CompletionStage<String> findAccountForVerificationRequest(VerificationRequest request) {
        return pendingRequestStore
                .tryRemove(request.getId().toString())
                .thenApply(maybeAccountId -> maybeAccountId.orElseThrow(NotFoundException::new));
    }

    /** Verifies the account with the localized {@link VerifiedUser}, unless a duplicate verification occurs. */
    private CompletionStage<Void> verifyAccount(String accountId, VerifiedUser localizedUser) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(config.verifiedAccountExpiresIn());
        return verificationStore
                .trySave(accountId, localizedUser, expiration)
                .thenAccept(maybeDuplicateAccountId -> maybeDuplicateAccountId.ifPresent(a -> {
                    throw new ClientErrorException(409);
                }));
    }
}
