package org.example.age.avs.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.avs.spi.VerifiedAccount;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.spi.PendingStore;
import org.example.age.common.spi.PendingStoreRepository;

/** Manages the age verification protocol. */
@Singleton
final class AvsVerificationManager {

    private final AvsVerifiedAccountStore accountStore;
    private final PendingStore<VerificationRequest> pendingUnlinkedRequestStore;
    private final PendingStore<VerificationRequest> pendingLinkedRequestStore;
    private final AvsVerifiedUserLocalizer userLocalizer;
    private final AvsEndpointConfig config;

    @Inject
    public AvsVerificationManager(
            AvsVerifiedAccountStore accountStore,
            PendingStoreRepository pendingStores,
            AvsVerifiedUserLocalizer userLocalizer,
            AvsEndpointConfig config) {
        this.accountStore = accountStore;
        this.pendingUnlinkedRequestStore = pendingStores.get("unlinked-request", VerificationRequest.class);
        this.pendingLinkedRequestStore = pendingStores.get("linked-request", VerificationRequest.class);
        this.userLocalizer = userLocalizer;
        this.config = config;
    }

    /** Creates a verification request for a site. */
    public CompletionStage<VerificationRequest> createVerificationRequest(String siteId) {
        VerificationRequest request = generateVerificationRequest(siteId);
        return pendingUnlinkedRequestStore
                .put(request.getId().toString(), request, request.getExpiration())
                .thenApply(__ -> request);
    }

    /** Links a verification request to an account. */
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId, String accountId) {
        return accountStore
                .load(accountId)
                .thenCompose(__ -> removePending(pendingUnlinkedRequestStore, requestId.toString()))
                .thenCompose(request -> pendingLinkedRequestStore.put(accountId, request, request.getExpiration()));
    }

    /** Creates an age certificate for an account. */
    public CompletionStage<AgeCertificate> createAgeCertificate(String accountId) {
        CompletionStage<VerifiedAccount> accountStage = accountStore.load(accountId);
        CompletionStage<VerificationRequest> requestStage =
                accountStage.thenCompose(account -> removePending(pendingLinkedRequestStore, account.id()));
        CompletionStage<VerifiedUser> localizedUserStage =
                accountStage.thenCombine(requestStage, this::localize).thenCompose(Function.identity());
        return requestStage.thenCombine(localizedUserStage, AvsVerificationManager::createAgeCertificate);
    }

    /** Generates a verification request for a site. */
    private VerificationRequest generateVerificationRequest(String siteId) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(config.verificationRequestExpiresIn());
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Localizes the pseudonymous user data. */
    private CompletionStage<VerifiedUser> localize(VerifiedAccount account, VerificationRequest request) {
        String siteId = request.getSiteId();
        AgeThresholds ageThresholds = config.ageThresholds().get(siteId);
        VerifiedUser ageRangeUser = ageThresholds.anonymize(account.user());
        return userLocalizer.localize(ageRangeUser, siteId);
    }

    /** Creates an age certificate. */
    private static AgeCertificate createAgeCertificate(VerificationRequest request, VerifiedUser localizedUser) {
        return AgeCertificate.builder().request(request).user(localizedUser).build();
    }

    /** Removes a value from a pending store, or throws {@link NotFoundException}. */
    private static <V> CompletionStage<V> removePending(PendingStore<V> pendingStore, String key) {
        return pendingStore.tryRemove(key).thenApply(maybeValue -> maybeValue.orElseThrow(NotFoundException::new));
    }
}
