package org.example.age.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.client.SiteClientRepository;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.api.request.AccountIdExtractor;
import org.example.age.service.api.store.AvsVerifiedUserStore;
import org.example.age.service.api.store.PendingStore;
import org.example.age.service.api.store.PendingStoreRepository;
import org.example.age.service.util.AsyncCalls;
import retrofit2.Call;

@Singleton
final class AvsService implements AvsApi {

    private final AccountIdExtractor accountIdExtractor;
    private final SiteClientRepository siteClients;
    private final AvsVerifiedUserStore userStore;
    private final PendingStore<VerificationRequest> pendingUnlinkedRequestStore;
    private final PendingStore<VerificationRequest> pendingLinkedRequestStore;
    private final AgeCertificateSigner ageCertificateSigner;
    private final AvsVerifiedUserLocalizer userLocalizer;
    private final AvsServiceConfig config;

    @Inject
    public AvsService(
            AccountIdExtractor accountIdExtractor,
            SiteClientRepository siteClients,
            AvsVerifiedUserStore userStore,
            PendingStoreRepository pendingStores,
            AgeCertificateSigner ageCertificateSigner,
            AvsVerifiedUserLocalizer userLocalizer,
            AvsServiceConfig config) {
        this.accountIdExtractor = accountIdExtractor;
        this.siteClients = siteClients;
        this.userStore = userStore;
        this.pendingUnlinkedRequestStore = pendingStores.get("unlinked-request", VerificationRequest.class);
        this.pendingLinkedRequestStore = pendingStores.get("linked-request", VerificationRequest.class);
        this.ageCertificateSigner = ageCertificateSigner;
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(
            String siteId, AuthMatchData authMatchData) {
        return validateSite(siteId)
                .thenApply(this::createVerificationRequestForSite)
                .thenCompose(this::storeUnlinkedVerificationRequest);
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        String accountId = accountIdExtractor.getForRequest();
        return loadVerifiedUser(accountId).thenCompose(user -> linkAccountToVerificationRequest(accountId, requestId));
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        String accountId = accountIdExtractor.getForRequest();
        CompletionStage<VerifiedUser> userStage = loadVerifiedUser(accountId);
        CompletionStage<VerificationRequest> requestStage =
                userStage.thenCompose(user -> findVerificationRequestForAccount(accountId));
        CompletionStage<VerifiedUser> localizedUserStage = userStage
                .thenCombine(requestStage.thenApply(VerificationRequest::getSiteId), userLocalizer::localize)
                .thenCompose(Function.identity());
        return requestStage
                .thenCombine(localizedUserStage, (request, localizedUser) -> AgeCertificate.builder()
                        .request(request)
                        .user(localizedUser)
                        .build())
                .thenCompose(ageCertificateSigner::sign)
                .thenCompose(this::sendSignedAgeCertificate);
    }

    /** Validates the site. */
    private CompletableFuture<String> validateSite(String siteId) {
        try {
            siteClients.get(siteId);
            return CompletableFuture.completedFuture(siteId);
        } catch (NotAuthorizedException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /** Loads the {@link VerifiedUser} for the account. */
    private CompletionStage<VerifiedUser> loadVerifiedUser(String accountId) {
        return userStore.tryLoad(accountId).thenApply(maybeUser -> maybeUser.orElseThrow(ForbiddenException::new));
    }

    /** Creates a {@link VerificationRequest} for the site. */
    private VerificationRequest createVerificationRequestForSite(String siteId) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(config.verificationRequestExpiresIn());
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Stores an unlinked {@link VerificationRequest}. */
    private CompletionStage<VerificationRequest> storeUnlinkedVerificationRequest(VerificationRequest request) {
        return pendingUnlinkedRequestStore
                .put(request.getId().toString(), request, request.getExpiration())
                .thenApply(v -> request);
    }

    /** Links the account to a stored {@link VerificationRequest}. */
    private CompletionStage<Void> linkAccountToVerificationRequest(String accountId, SecureId requestId) {
        return pendingUnlinkedRequestStore
                .tryRemove(requestId.toString())
                .thenApply(maybeRequest -> maybeRequest.orElseThrow(NotFoundException::new))
                .thenCompose(request -> pendingLinkedRequestStore.put(accountId, request, request.getExpiration()));
    }

    /** Finds the {@link VerificationRequest} that is linked to the account. */
    private CompletionStage<VerificationRequest> findVerificationRequestForAccount(String accountId) {
        return pendingLinkedRequestStore
                .tryRemove(accountId)
                .thenApply(maybeRequest -> maybeRequest.orElseThrow(NotFoundException::new));
    }

    /** Sends a {@link SignedAgeCertificate} to the corresponding site. */
    private CompletionStage<Void> sendSignedAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        String siteId = signedAgeCertificate.getAgeCertificate().getRequest().getSiteId();
        SiteApi siteClient = siteClients.get(siteId);
        Call<Void> certificateCall = siteClient.processAgeCertificate(signedAgeCertificate);
        return AsyncCalls.make(certificateCall, errorCode -> (errorCode != 410) ? 500 : 404);
    }
}
