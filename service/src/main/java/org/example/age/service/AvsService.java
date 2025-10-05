package org.example.age.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.client.AsyncCalls;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.common.spi.PendingStore;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.site.api.client.SiteApi;
import retrofit2.Call;

@Singleton
final class AvsService implements AvsApi {

    private final AccountIdContext accountIdContext;
    private final Map<String, SiteApi> siteClients;
    private final AvsVerifiedUserStore userStore;
    private final PendingStore<VerificationRequest> pendingUnlinkedRequestStore;
    private final PendingStore<VerificationRequest> pendingLinkedRequestStore;
    private final AgeCertificateSigner ageCertificateSigner;
    private final AvsVerifiedUserLocalizer userLocalizer;
    private final AvsServiceConfig config;

    @Inject
    public AvsService(
            AccountIdContext accountIdContext,
            Map<String, SiteApi> siteClients,
            AvsVerifiedUserStore userStore,
            PendingStoreRepository pendingStores,
            AgeCertificateSigner ageCertificateSigner,
            AvsVerifiedUserLocalizer userLocalizer,
            AvsServiceConfig config) {
        this.accountIdContext = accountIdContext;
        this.siteClients = siteClients;
        this.userStore = userStore;
        this.pendingUnlinkedRequestStore = pendingStores.get("unlinked-request", VerificationRequest.class);
        this.pendingLinkedRequestStore = pendingStores.get("linked-request", VerificationRequest.class);
        this.ageCertificateSigner = ageCertificateSigner;
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
        getSiteClient(siteId); // check that the site is registered
        VerificationRequest request = createVerificationRequest(siteId);
        return storeUnlinkedVerificationRequest(request);
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        return loadVerifiedAccount()
                .thenApply(VerifiedAccount::id)
                .thenCompose(accountId -> linkAccountToVerificationRequest(accountId, requestId));
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        CompletionStage<VerifiedAccount> accountStage = loadVerifiedAccount();
        CompletionStage<VerificationRequest> requestStage =
                accountStage.thenApply(VerifiedAccount::id).thenCompose(this::findVerificationRequestForAccount);
        CompletionStage<VerifiedUser> localizedUserStage = accountStage
                .thenApply(VerifiedAccount::user)
                .thenCombine(requestStage.thenApply(VerificationRequest::getSiteId), this::localize)
                .thenCompose(Function.identity());
        return requestStage
                .thenCombine(localizedUserStage, (request, localizedUser) -> AgeCertificate.builder()
                        .request(request)
                        .user(localizedUser)
                        .build())
                .thenCompose(ageCertificateSigner::sign)
                .thenCompose(this::sendSignedAgeCertificate);
    }

    /** Gets the client for the site, or throws {@link NotFoundException} if the site is not registered. */
    private SiteApi getSiteClient(String siteId) {
        return Optional.ofNullable(siteClients.get(siteId)).orElseThrow(NotFoundException::new);
    }

    /** Loads a verified account. */
    private CompletionStage<VerifiedAccount> loadVerifiedAccount() {
        String accountId = accountIdContext.getForRequest();
        return userStore
                .tryLoad(accountId)
                .thenApply(maybeUser -> maybeUser.orElseThrow(ForbiddenException::new))
                .thenApply(user -> new VerifiedAccount(accountId, user));
    }

    /** Localizes a {@link VerifiedUser} for the site. */
    private CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId) {
        AgeThresholds ageThresholds =
                Optional.ofNullable(config.ageThresholds().get(siteId)).orElseThrow(NotFoundException::new);
        return userLocalizer.localize(ageThresholds.anonymize(user), siteId);
    }

    /** Creates a {@link VerificationRequest} for the site. */
    private VerificationRequest createVerificationRequest(String siteId) {
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
        SiteApi siteClient = getSiteClient(siteId);
        Call<Void> call = siteClient.processAgeCertificate(signedAgeCertificate);
        return AsyncCalls.make(call)
                .exceptionallyCompose(t -> CompletableFuture.failedFuture(
                        (!(t instanceof WebApplicationException e)
                                        || (e.getResponse().getStatus() != 404))
                                ? new InternalServerErrorException(t)
                                : t));
    }

    /** Account with a {@link VerifiedUser}. */
    private record VerifiedAccount(String id, VerifiedUser user) {}
}
