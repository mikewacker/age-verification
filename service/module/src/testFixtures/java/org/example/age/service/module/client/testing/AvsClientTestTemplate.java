package org.example.age.service.module.client.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.api.client.SiteApi;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public abstract class AvsClientTestTemplate {

    @Test
    public void useSiteClient() throws IOException {
        SiteApi siteClient = siteClients().get("site");
        Response<VerificationState> response = siteClient.getVerificationState().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void error_UnregisteredSiteClient() {
        assertThatThrownBy(() -> siteClients().get("unregistered-site")).isInstanceOf(NotFoundException.class);
    }

    protected abstract SiteClientRepository siteClients();

    /** Stub service implementation of {@link org.example.age.site.api.SiteApi}. */
    public static final class StubSiteService implements org.example.age.site.api.SiteApi {

        @Override
        public CompletionStage<VerificationState> getVerificationState() {
            VerificationState state = VerificationState.builder()
                    .status(VerificationStatus.UNVERIFIED)
                    .build();
            return CompletableFuture.completedFuture(state);
        }

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequest() {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }
    }
}
