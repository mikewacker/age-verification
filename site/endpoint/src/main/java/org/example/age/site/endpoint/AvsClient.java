package org.example.age.site.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.InternalServerErrorException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.client.AsyncCalls;
import retrofit2.Call;

/** Client for the age verification service. */
@Singleton
final class AvsClient {

    private final AvsApi client;
    private final SiteEndpointConfig config;

    @Inject
    public AvsClient(AvsApi client, SiteEndpointConfig config) {
        this.client = client;
        this.config = config;
    }

    /** Creates a verification request. */
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        Call<VerificationRequest> call = client.createVerificationRequestForSite(config.id());
        return AsyncCalls.make(call).exceptionallyCompose(AvsClient::onCreateVerificationRequestError);
    }

    /** Handles an error creating the verification request. */
    private static CompletionStage<VerificationRequest> onCreateVerificationRequestError(Throwable t) {
        return CompletableFuture.failedFuture(new InternalServerErrorException(t));
    }
}
