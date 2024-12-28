package org.example.age.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.crypto.SecureId;

public final class SiteService implements SiteApi {

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        VerificationState state = VerificationState.builder()
                .status(VerificationStatus.UNVERIFIED)
                .build();
        return CompletableFuture.completedFuture(state);
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        VerificationRequest request = VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        return CompletableFuture.completedFuture(request);
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        return CompletableFuture.completedFuture(null);
    }
}
