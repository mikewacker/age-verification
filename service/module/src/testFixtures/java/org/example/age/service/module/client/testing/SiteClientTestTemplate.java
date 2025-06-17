package org.example.age.service.module.client.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationRequest;
import org.example.age.api.client.AvsApi;
import org.example.age.api.crypto.SecureId;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public abstract class SiteClientTestTemplate {

    @Test
    public void useAvsClient() throws IOException {
        Response<Void> response = avsClient().sendAgeCertificate().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    protected abstract AvsApi avsClient();

    /** Stub service implementation of {@link org.example.age.api.AvsApi}. */
    public static final class StubAvsService implements org.example.age.api.AvsApi {

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> sendAgeCertificate() {
            return CompletableFuture.completedFuture(null);
        }
    }
}
