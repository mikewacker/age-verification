package org.example.age.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;

public final class AvsService implements AvsApi {

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(
            String siteId, AuthMatchData authMatchData) {
        VerificationRequest request = VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        return CompletableFuture.completedFuture(request);
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        return CompletableFuture.completedFuture(null);
    }
}
